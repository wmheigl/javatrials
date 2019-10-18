/**
 * MatrixVector.java
 *
 * @author Werner M. Heigl
 * @version 2019.09.27
 */
package CommonsMath;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * This class tests some of the vector matrix operations in Commons Math Linear
 * Algebra.
 * 
 */
public class MatrixVector {

	private static final RealVector RECEIVER = new ArrayRealVector(new double[] { 1, 1, 1 });
	private static final RealVector SOURCE = new ArrayRealVector(new double[] { 0, 0, 0 });
	private static final RealMatrix M = new Array2DRowRealMatrix(
			new double[][] { { 0, 1, 0 }, { 0, 0, 1 }, { 1, 0, 0 } });

	public static void main(String[] args) {

		RealVector G = RECEIVER.subtract(SOURCE);

		assert G.unitVector().getNorm() == 1.0 : "G not a unit vector";

		assert G.dotProduct(M.operate(G)) == 3.0 : "G.M.G^T not equal to 3.0";

		assert G.mapMultiply(G.dotProduct(M.operate(G)))
				.equals(new ArrayRealVector(new double[] { 3, 3, 3 })) : "(G.M.G^T)G not equal to {3,3,3}";

		assert M.transpose().preMultiply(G)
				.equals(new ArrayRealVector(new double[] { 1, 1, 1 })) : "G.M^T not equal to {1,1,1}";

		assert G.mapMultiply(G.dotProduct(M.operate(G))).subtract(M.transpose().preMultiply(G))
				.equals(new ArrayRealVector(new double[] { 2, 2, 2 })) : "(G.M.G^T)G-G.M^T not equal to {2,2,2}";

		assert G.mapMultiply(G.dotProduct(M.operate(G)))
				.dotProduct(M.transpose().preMultiply(G)) == 0 : "(G.M.G^T)G and G.M^T not orthogonal";

		System.out.println("Success");
	}

}
