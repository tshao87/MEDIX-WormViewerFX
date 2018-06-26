package annotationtoolfx.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import annotationtoolfx.object.FrameAnnotationInfo;
import annotationtoolfx.object.FrameAnnotationManager;

public class FrameManagerLoader {
	
	private String strainId;
	private File wormImageDirectory;
	private String compareAnnSet;
	private String editAnnSet;
	private File predictedAnnFile;
	private int predColIndex;
	private File offlineAnnFile;
	
	private FrameAnnotationManager frameMgr;

	public FrameManagerLoader() {
		predColIndex = -1;
	}
	
	public FrameAnnotationManager getFrameMgr() {
		return frameMgr; 
	}
	
	public void setStrainId(String strainId) {
		this.strainId = strainId;
	}

	public void setDirectory(String imagesPath) {
		try {
			File f = new File(imagesPath);
			wormImageDirectory = f;
		}
		catch(Exception e) {
			wormImageDirectory = null;
		}
	}

	public void setDirectory(File wormImageDirectory) {
		this.wormImageDirectory = wormImageDirectory;
	}	
	
	public void setPredictionFile(File predictedAnnFile) {
		this.predictedAnnFile = predictedAnnFile;
	}
	
	public void setPredictionColumn(int predColIndex) {
		this.predColIndex = predColIndex;
	}

	public void setPredictionFile(String predictionFile) {
		try {
			File f = new File(predictionFile);
			predictedAnnFile = f;
		}
		catch(Exception e) {
			predictedAnnFile = null;
		}
	}

	public boolean hasValidImagesPath() {
		if(wormImageDirectory != null)
			return wormImageDirectory.exists();
		return false;
	}
	
	public void setCompareAnnSet(String compareAnnSet) {
		this.compareAnnSet = compareAnnSet;
	}

	public void setEditAnnSet(String editAnnSet) {
		this.editAnnSet = editAnnSet;
	}

	public boolean LoadAnnFrameMgr() {
		String query2Ann = "SELECT imageinfo.frameid, imagenumber, timeelapsed, annotation, setid FROM imageinfo " + 
				"JOIN Annotations ON imageinfo.frameid = Annotations.frameid AND (Annotations.setid = ? OR Annotations.setid = ?) AND imageinfo.straintypeid = ? " + 
				"ORDER BY imageinfo.imagenumber";
		String query1Ann = "SELECT imageinfo.frameid, imagenumber, timeelapsed, annotation, setid FROM imageinfo " + 
				"JOIN Annotations ON imageinfo.frameid = Annotations.frameid AND Annotations.setid = ? AND imageinfo.straintypeid = ? " + 
				"ORDER BY imageinfo.imagenumber;";
		String query0Ann = "SELECT imageinfo.frameid, imagenumber, timeelapsed FROM imageinfo WHERE straintypeid = ? ORDER BY imagenumber";

		String annSetQuery = "SELECT setid FROM annotationset WHERE name = ?";

		PreparedStatement stmt = null;
		PreparedStatement annSetStmt = null;
		
		ArrayList<FrameAnnotationInfo> list = new ArrayList<FrameAnnotationInfo>();
		HashMap<String, FrameAnnotationInfo> map = new HashMap<String, FrameAnnotationInfo>();
	
		ResultSet rs = null, rs1 = null;
                String header = Utilities.getPrefixZeros(strainId);
		
		try {
		
			boolean compare = compareAnnSet != null  && compareAnnSet.length() > 0;
			boolean edit = editAnnSet != null  && editAnnSet.length() > 0;
			if(offlineAnnFile != null &&  offlineAnnFile.exists()) {
				compare = false;
				edit = false;
			}
			
			//Get setids
			annSetStmt = ConnectionSingleton.getConnectionInstance().getConnection().prepareStatement(annSetQuery);
			if(compare) {
				annSetStmt.setString(1, compareAnnSet);
		    	rs1 = annSetStmt.executeQuery();
		    	rs1.next();
		    	compareAnnSet = rs1.getString(1);
		    	rs1.close();
			}	    	

			if(edit) {
				annSetStmt.setString(1, editAnnSet);
		    	rs1 = annSetStmt.executeQuery();
		    	rs1.next();
		    	editAnnSet = rs1.getString(1);
			}
	    	
			String query = "";
			if(compare && edit)
				query = query2Ann;
			else if(compare || edit)
				query = query1Ann;
			else
				query = query0Ann;
			
			stmt = ConnectionSingleton.getConnectionInstance().getConnection().prepareStatement(query);
			
			int index = 0;
			
			if(compare) {
				index += 1;
				stmt.setString(index, compareAnnSet);
			}
			if(edit) {
				index += 1;
				stmt.setString(index, editAnnSet);
			}
			index += 1;
			stmt.setString(index, strainId);
			rs = stmt.executeQuery();
	
			String set;
			int frameNo = 0;
			String frameNoStr;
			FrameAnnotationInfo fai;
			while(rs.next()){
				frameNo = rs.getInt(2);
				frameNoStr = Integer.toString(frameNo);
				if(!map.containsKey(frameNoStr)) {
		           
					fai = new FrameAnnotationInfo();
		            fai.setElapsedTime(String.format("%f", rs.getDouble(3)));
		            fai.setFrameNo(rs.getInt(2));
		            fai.setdbFrameId(rs.getString(1));
			    	list.add(fai);			    	
					map.put(frameNoStr, fai);
				}
				else {
					fai = map.get(frameNoStr);
				}

				if(compare || edit) {
					set = rs.getString(5);
					if(set.equals(compareAnnSet)) {
						fai.setHumanAnnotation(rs.getString(4));
					}
					else if (set.equals(editAnnSet)) {
						fai.setUpdatedAnnotation(rs.getString(4));
					}
				}
		    	String s = String.format("%d", rs.getInt(2)).toString();
		    	s = (header + s).substring(s.length());
		    	
		    	fai.setImageFile(new File(String.format("%s/%s.jpeg", wormImageDirectory.getAbsolutePath(), s)));
                        
			}
			
			String line;
			String[] values;
			String frameId;
			String val;
			FileReader fileReader;
			BufferedReader bufferedReader = null;

			if(offlineAnnFile != null && offlineAnnFile.exists()) {
				fileReader = new FileReader(offlineAnnFile);
				bufferedReader = new BufferedReader(fileReader);
				while((line = bufferedReader.readLine()) != null) {
					values = line.split(",");
					frameId = values[0].trim();
					if(map.containsKey(frameId)) {
						fai = map.get(frameId);
						val = values[1].trim();
						if(val.length() > 0)
							fai.setHumanAnnotation(val);
						val = values[2].trim();
						if(val.length() > 0)
							fai.setPredictedAnnotation(val);
						val = values[3].trim();
						if(val.length() > 0)
							fai.setUpdatedAnnotation(val);
					}
				}
		    	bufferedReader.close();
		    	fileReader.close();

			}
			else if(predictedAnnFile != null && predColIndex > -1) {
				fileReader = new FileReader(predictedAnnFile);
				bufferedReader = new BufferedReader(fileReader);

		    	if(predictedAnnFile != null) {
					while((line = bufferedReader.readLine()) != null) {
						values = line.split(",");
						frameId = values[0].trim();
						if(map.containsKey(frameId)) {
							fai = map.get(frameId);
							fai.setPredictedAnnotation(values[predColIndex]);
						}
					}
		    	}
		    	bufferedReader.close();
		    	fileReader.close();
			}
		}
		catch(Exception e){
			return false;  
		}
		finally {
			try
			{
		      rs.close();
		      rs1.close();
		      stmt.close();
		      annSetStmt.close();
			}
			catch(Exception e){
				
			}
		}
		
		
		
		
		if(editAnnSet != null && editAnnSet.trim().length() > 0)
			frameMgr = new FrameAnnotationManager(list, strainId, editAnnSet);
		else
			frameMgr = new FrameAnnotationManager(list, strainId);
	    return true;
	}

	public void setOfflineAnnFile(File offlineAnnFile) {
		this.offlineAnnFile = offlineAnnFile;
	}



	
}
