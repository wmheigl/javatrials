/**
 * Hdf5Files.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.16
 */
package FilesAndDirs;

import hdf.hdf5lib.*;
import hdf.hdf5lib.exceptions.HDF5LibraryException;

/**
 * This class illustrates how to read HDF5 files.
 * 
 */
public class Hdf5Files {

  private static final String DIR = "/meg2/ben/promax_data_home/MachineLearning_Synth_Field/PSEvents_2mil_750x500/";
  private static final String FILE = "FFID-000001_AMP-1.0000_CON-0.0_X-3142050.0_Y-1331668.8_ELEV--2368.1_T-1591734621255_CLASS-0.h5";
  
  /**
   * @param args A dummy argument.
   * @throws NullPointerException if
   * @throws HDF5LibraryException 
   */
  public static void main(String[] args) {

    final String FILENAME = DIR + FILE;
    
    try {
      
      int file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);
      
      if (file_id >= 0)
        H5.H5Fclose(file_id);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

}
