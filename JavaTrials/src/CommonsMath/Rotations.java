/**
 * Rotations.java
 *
 * @author Werner M. Heigl
 * @version 2019.10.08
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
 * Its purpose was to understand how to rotate a vertical receiver array into a deviated well given
 * well azimuth and inclination.
 *
 */
public class Rotations {

  private static RotationConvention convention = RotationConvention.VECTOR_OPERATOR;

  public static void main(String[] args) {

    /*
     * Verify that rotations are counter-clockwise when looking along the rotation axis towards the
     * origin.
     */
    assert Vector3D.PLUS_J.equals(
        new Rotation(Vector3D.PLUS_K, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR)
            .applyTo(Vector3D.PLUS_I));
    assert Vector3D.PLUS_K.equals(
        new Rotation(Vector3D.PLUS_I, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR)
            .applyTo(Vector3D.PLUS_J));
    assert Vector3D.PLUS_I.equals(
        new Rotation(Vector3D.PLUS_J, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR)
            .applyTo(Vector3D.PLUS_K));

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
        new Rotation(Vector3D.PLUS_K, Math.toRadians(90), RotationConvention.VECTOR_OPERATOR)
            .getMatrix());
    RealMatrix rp = new Array2DRowRealMatrix(new Rotation(r1.getData(), 1e-12)
        .compose(new Rotation(r2.getData(), 1e-12), RotationConvention.VECTOR_OPERATOR)
        .getMatrix());
    System.out.println("Rotation from (N,E,Down) to (E,N,Up):");
    System.out.println("R\u2218R' = " + rp);
    System.out.println("R\u2218R'^T = " + rp.transpose());
    System.out.println("R'.M'.R'^T = " + rp.multiply(m.multiply(rp.transpose())));
    System.out.println("R'.M'.R'   = " + rp.multiply(m.multiply(rp)));
    System.out.println();

    double azimuth = Math.toRadians(60);
    double inclination = Math.toRadians(30);

    /*
     * Rotating vectors.
     * 
     */
    Vector3D first = new Vector3D(0, 0, 0);
    Vector3D last1 = new Vector3D(0, 0, 1);
    Vector3D line1 = last1.subtract(first);
    Vector3D last2 = new Vector3D(1, 0, 0);
    Vector3D line2 = last2.subtract(first);

    Rotation r_inc_j = new Rotation(Vector3D.PLUS_J, inclination, convention);
    Rotation r_azi_k = new Rotation(Vector3D.PLUS_K, azimuth, convention);

    Vector3D result_inc = r_inc_j.applyTo(line1);
    System.out.println("rotate " + line1 + " around j-axis: " + result_inc);

    Vector3D result_azi = r_azi_k.applyTo(line2);
    System.out.println("rotate " + line2 + " around k-axis: " + result_azi);

    Vector3D result_inc_azi = r_azi_k.compose(r_inc_j, convention).applyTo(line1);
    System.out.println("rotate " + line1 + " around j-axis and then k-axis: " + result_inc_azi);
    System.out.println("check: " + r_azi_k.applyTo(result_inc));

    Vector3D result_azi_inc = r_inc_j.compose(r_azi_k, convention).applyTo(line1);
    System.out.println("rotate " + line1 + " around k-axis and then j-axis: " + result_azi_inc);

    System.out.println("azimuth: " + Math.toDegrees(result_inc_azi.getAlpha()));
    System.out.println("inclination: " + (90 - Math.toDegrees(result_inc_azi.getDelta())));

  }

}
