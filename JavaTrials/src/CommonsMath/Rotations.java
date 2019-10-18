/**
 * Rotations.java
 *
 * @author Werner M. Heigl
 * @version 2019.10.18
 */
package CommonsMath;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Class illustrating rotation of vectors with Commons Math Geometry package.
 * <p>
 * Its purpose was to understand how to rotate a vertical receiver array into a deviated well given well azimuth and
 * inclination.
 *
 */
public class Rotations {

  private static RotationConvention convention = RotationConvention.VECTOR_OPERATOR;

  public static void main(String[] args) {

    /*
     * Verify that rotations are counter-clockwise when looking along the rotation axis towards the origin.
     */
    assert Vector3D.distance(Vector3D.PLUS_J,
	new Rotation(Vector3D.PLUS_K, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR).applyTo(Vector3D.PLUS_I)) < 1e-12;
    assert Vector3D.distance(Vector3D.PLUS_K,
	new Rotation(Vector3D.PLUS_I, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR).applyTo(Vector3D.PLUS_J)) < 1e-12;
    assert Vector3D.distance(Vector3D.PLUS_I,
	new Rotation(Vector3D.PLUS_J, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR).applyTo(Vector3D.PLUS_K)) < 1e-12;

    // @formatter:off
    /*
     * To rotate a moment tensor from (N,E,Down) to (S,E,Up) system of
     * coordinates requires a rotation of 180 deg about the x2 axis. The
     * rotation matrix is
     * 
     *       (-1  0  0) 
     *   R = ( 0  1  0)
     *       ( 0  0 -1)
     * 
     * as given in eq. 64 of Pujol & Herrmann (1990). The rotated moment
     * tensor is
     * 
     *   M'_ij = R_ik . M_kl . R_jl  or  M' = R . M . R^T
     * 
     * In Java this is accomplished as follows:
     */
    RealMatrix r1 = new Array2DRowRealMatrix(
        new Rotation(Vector3D.PLUS_J, Math.toRadians(180), RotationConvention.VECTOR_OPERATOR)
            .getMatrix());
    RealMatrix m = new Array2DRowRealMatrix(new double[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } });
    System.out.println("Rotation of M from (N,E,Down) to (S,E,Up):");
    System.out.println("R = " + r1);
    System.out.println("R^T = " + r1.transpose());
    System.out.println("R.M.R^T = " + r1.multiply(m.multiply(r1.transpose())));
    System.out.println("R.M.R   = " + r1.multiply(m.multiply(r1)));
    System.out.println();

    /*
     * To rotate the above into a (E,N,Up) system of coordinates requires a rotation of 90 deg about the vertical axis.
     * The rotation matrix is
     * 
     *        ( 0 -1  0) 
     *   R' = ( 1  0  0)
     *        ( 0  0  1)
     * 
     * The combined rotation matrix is
     * 
     *          (0  1  0)
     *   R.R' = (1  0  0)
     *          (0  0 -1)
     *          
     * The following shows how to find the rotation matrix and how to apply it.
     */
    // @formatter:on
    RealMatrix r2 = new Array2DRowRealMatrix(
	new Rotation(Vector3D.PLUS_K, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR).getMatrix());
    RealMatrix rp = new Array2DRowRealMatrix(
	new Rotation(r1.getData(), 1e-12).compose(new Rotation(r2.getData(), 1e-12), RotationConvention.VECTOR_OPERATOR).getMatrix());
    System.out.println("Rotation from (N,E,Down) to (E,N,Up):");
    System.out.println("R\u2218R' = " + rp);
    System.out.println("R\u2218R'^T = " + rp.transpose());
    System.out.println("R'.M'.R'^T = " + rp.multiply(m.multiply(rp.transpose())));
    System.out.println("R'.M'.R'   = " + rp.multiply(m.multiply(rp)));
    System.out.println();

    /*
     * Rotating vectors.
     * 
     */
    Vector3D first = new Vector3D(0, 0, -1);

    Rotation r_inc_j = new Rotation(Vector3D.PLUS_J, Math.toRadians(-30), convention);
    Rotation r_azi_k = new Rotation(Vector3D.PLUS_K, Math.toRadians(-90), convention);

    Vector3D result_inc = r_azi_k.applyTo(r_inc_j.applyTo(first));
    
    System.out.println("incline " + first + " around j-axis: " + r_inc_j.applyTo(first));
    System.out.println("rotate inclined " + r_inc_j.applyTo(first) + " around k-axis: " + result_inc);


  }

}
