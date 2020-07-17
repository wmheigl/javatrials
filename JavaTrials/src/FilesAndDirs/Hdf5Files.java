/**
 * Hdf5Files.java
 *
 * @author Werner M. Heigl
 * @version 2020.07.17
 */
package FilesAndDirs;

import java.util.Arrays;

import FilesAndDirs.H5Ex_D_Chunk.H5D_layout;
import hdf.hdf5lib.*;

/**
 * This class illustrates how to read HDF5 files.
 * 
 */
public class Hdf5Files {

  private static final String DIR = "/meg2/ben/promax_data_home/MachineLearning_Synth_Field/PSEvents_2mil_750x500/";
  private static final String FILE = "FFID-000001_AMP-1.0000_CON-0.0_X-3142050.0_Y-1331668.8_ELEV--2368.1_T-1591734621255_CLASS-0.h5";
  private static final int DIM_X = 500;
  private static final int DIM_Y = 750;

  /**
   * @param args A dummy argument.
   */
  public static void main(String[] args) {

    final String FILENAME = DIR + FILE;
    final String DATASETNAME = "NanoSeisSingleFrameData/TraceData/TracesSamples";

    int file_id = -1;
    int dataset_id = -1;
    int dcpl_id = -1;
    int space_id = -1;
    int attr_id = -1;
    float[][] dset_data = null;

    try {

      // open resources
      file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);
      if (file_id >= 0) {
	dataset_id = H5.H5Dopen(file_id, DATASETNAME, HDF5Constants.H5P_DEFAULT);
      }

      // get the storage layout
      if (dataset_id >= 0) {
	dcpl_id = H5.H5Dget_create_plist(dataset_id);
	int layout_type = H5.H5Pget_layout(dcpl_id);
	System.out.println("Storage layout for '" + DATASETNAME + "' is: " + H5D_layout.get(layout_type));
      }

      // get the dataspace dimensions
      if (dataset_id >= 0) {
	space_id = H5.H5Dget_space(dataset_id);
	int ndims = H5.H5Sget_simple_extent_ndims(space_id);
	System.out.println("No. of dimensions: " + ndims);
	long[] dims = new long[ndims];
	long[] maxdims = new long[ndims];
	H5.H5Sget_simple_extent_dims(space_id, dims, maxdims);
	System.out.println("Dimensions: " + Arrays.toString(dims));
	System.out.println("Max. Dimensions: " + Arrays.toString(maxdims));
	dset_data = new float[(int) dims[0]][(int) dims[1]];
      }

      // get the dataspace attribute
      if (dataset_id >= 0) {
	attr_id = H5.H5Aopen_by_name(dataset_id, ".", "ActualTracesInFrame", HDF5Constants.H5P_DEFAULT, HDF5Constants.H5P_DEFAULT);
	long[] ntraces = new long[1];
	H5.H5Aread(attr_id, HDF5Constants.H5T_NATIVE_LONG, ntraces);
	System.out.println("Traces in file: " + ntraces[0]);
	if (attr_id >= 0) {
	  H5.H5Aclose(attr_id);
	}
      }

      // read the data frame
      if (dataset_id >= 0) {
	H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_FLOAT, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT,
	    dset_data);
      }

      // quick check
      System.out.println("First data point is: " + dset_data[0][0] + " ,  should be: 0.03183724");

      // close resources
      if (dcpl_id >= 0) {
	H5.H5Pclose(dcpl_id);
      }
      if (dataset_id >= 0) {
	H5.H5Dclose(dataset_id);
      }
      if (file_id >= 0) {
	H5.H5Fclose(file_id);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
