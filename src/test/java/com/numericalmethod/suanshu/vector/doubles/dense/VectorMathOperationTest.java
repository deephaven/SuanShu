package com.numericalmethod.suanshu.vector.doubles.dense;

import com.numericalmethod.suanshu.number.Real;
import com.numericalmethod.suanshu.vector.doubles.Vector;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Testcases for {@link VectorMathOperation}
 */
public class VectorMathOperationTest {
    @Test
    public void test_add() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v2 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertEquals(new DenseVector(1, 3, 5, 7, 9, 11, 13, 15, 17, 19), vectorMathOperation.add(v1, v2));

        Vector v5 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(new DenseVector(1, 3, 5, 7, 9, Double.NaN, 13, 15, 17, 19), vectorMathOperation.add(v1, v5));
        assertEquals(new DenseVector(), vectorMathOperation.add(new DenseVector(), new DenseVector()));
    }

    @Test
    public void test_minus() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v2 = new DenseVector(1, 3, 5, 7, 9, 11, 13, 15, 17, 19);

        assertEquals(new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), vectorMathOperation.minus(v2, v1));

        Vector v5 = new DenseVector(1, 3, 5, 7, 9, Double.NaN, 13, 15, 17, 19);
        assertEquals(new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10), vectorMathOperation.minus(v5, v1));
        assertEquals(new DenseVector(), vectorMathOperation.minus(new DenseVector(), new DenseVector()));
    }

    @Test
    public void test_multiply() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v2 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertEquals(new DenseVector(0, 2, 6, 12, 20, 30, 42, 56, 72, 90), vectorMathOperation.multiply(v1, v2));

        Vector v5 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(new DenseVector(0, 2, 6, 12, 20, Double.NaN, 42, 56, 72, 90), vectorMathOperation.multiply(v1, v5));
        assertEquals(new DenseVector(), vectorMathOperation.multiply(new DenseVector(), new DenseVector()));
    }

    @Test
    public void test_divide() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v2 = new DenseVector(5, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertEquals(new DenseVector(Double.POSITIVE_INFINITY, 2, 1.5, 4d / 3, 1.25, 1.2, 7d / 6, 8d / 7, 9d / 8, 10d / 9), vectorMathOperation.divide(v2, v1));

        Vector v5 = new DenseVector(-1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(new DenseVector(Double.NEGATIVE_INFINITY, 2, 1.5, 4d / 3, 1.25, Double.NaN, 7d / 6, 8d / 7, 9d / 8, 10d / 9), vectorMathOperation.divide(v5, v1));
        assertEquals(new DenseVector(), vectorMathOperation.divide(new DenseVector(), new DenseVector()));
    }

    @Test
    public void test_innerProduct() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v2 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        assertEquals(330d, vectorMathOperation.innerProduct(v1, v2), 0);

        Vector v5 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(Double.NaN, vectorMathOperation.innerProduct(v1, v5), 0);
        assertEquals(0d, vectorMathOperation.innerProduct(new DenseVector(), new DenseVector()), 0d);
    }

    @Test
    public void test_pow() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, Double.NaN, 5, 6, 7, 8, 9);

        assertEquals(new DenseVector(0, 1, 4, 9, Double.NaN, 25, 36, 49, 64, 81), vectorMathOperation.pow(v1, 2));

        Vector v5 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(new DenseVector(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN), vectorMathOperation.pow(v1, Double.NaN));
        assertEquals(new DenseVector(), vectorMathOperation.pow(new DenseVector(), 2));
    }

    @Test
    public void test_scaledDouble() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v3 = new DenseVector(0, 5, 10, 15, 20, 25, 30, 35, 40, 45);
        assertEquals(v3, vectorMathOperation.scaled(v1, 5));

        assertEquals(new DenseVector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), vectorMathOperation.scaled(v1, 0));

        Vector v5 = new DenseVector(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        assertEquals(v5, vectorMathOperation.scaled(v1, Double.NaN));
        assertEquals(new DenseVector(), vectorMathOperation.scaled(new DenseVector(), 2));
    }

    @Test
    public void test_addScalar() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v3 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertEquals(v3, vectorMathOperation.add(v1, 1));

        assertEquals(v1, vectorMathOperation.add(v1, 0));

        Vector v5 = new DenseVector(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        assertEquals(v5, vectorMathOperation.add(v1, Double.NaN));
        assertEquals(new DenseVector(), vectorMathOperation.add(new DenseVector(), 2));
    }

    @Test
    public void test_minusScalar() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Vector v3 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        assertEquals(v3, vectorMathOperation.minus(v1, 1));

        assertEquals(v1, vectorMathOperation.minus(v1, 0));

        Vector v5 = new DenseVector(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        assertEquals(v5, vectorMathOperation.minus(v1, Double.NaN));
        assertEquals(new DenseVector(), vectorMathOperation.minus(new DenseVector(), 2));
    }

    @Test
    public void test_scaledReal() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v3 = new DenseVector(Double.NaN, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double
                .POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        assertEquals(v3, vectorMathOperation.scaled(v1, new Real(new BigDecimal("2.35987454651E458"))));

        assertEquals(new DenseVector(0, 5, 10, 15, 20, 25, 30, 35, 40, 45), vectorMathOperation.scaled(v1, new Real(new BigInteger("5"))));
        assertEquals(new DenseVector(), vectorMathOperation.scaled(new DenseVector(), new Real(2d)));
    }

    @Test
    public void test_opposite() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Vector v3 = new DenseVector(0, -1, -2, -3, -4, -5, -6, -7, -8, -9);
        assertEquals(v3, vectorMathOperation.opposite(v1));

        Vector vz = new DenseVector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEquals(vz, vectorMathOperation.opposite(vz));

        Vector v5 = new DenseVector(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN);
        assertEquals(v5, vectorMathOperation.opposite(v5));
        assertEquals(new DenseVector(), vectorMathOperation.opposite(new DenseVector()));
    }

    @Test
    public void test_angle() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Vector v2 = new DenseVector(2, 4, 6, 2, 5, 7, 1, 9, 3, 10);
        assertEquals(0.5083530708262617, vectorMathOperation.angle(v1, v2), 1e-5);

        v2 = new DenseVector(2, 4, -6, 2, 5, -7, 1, 9, 3, -10);
        assertEquals(1.6018984946532318, vectorMathOperation.angle(v1, v2), 1e-5);

        Vector v5 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(Double.NaN, vectorMathOperation.innerProduct(v1, v5), 0);
        assertEquals(Double.NaN, vectorMathOperation.angle(new DenseVector(), new DenseVector()), 0d);
    }

    @Test
    public void test_norm() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        assertEquals(Math.sqrt(385), vectorMathOperation.norm(v1), 0);

        Vector vz = new DenseVector(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        assertEquals(0, vectorMathOperation.norm(vz), 0);

        Vector v2 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(Double.NaN, vectorMathOperation.norm(v2), 0);
        assertEquals(0d, vectorMathOperation.norm(new DenseVector()), 0d);
    }

    @Test
    public void test_normScalar() throws Exception {
        final VectorMathOperation vectorMathOperation = new VectorMathOperation();
        Vector v1 = new DenseVector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        final int p = 3;
        double expected = 0d;
        for (int i = 1; i <= v1.size(); i++) {
            expected += Math.pow(v1.get(i), p);
        }
        assertEquals(Math.pow(expected, 1d / p), vectorMathOperation.norm(v1, p), 0);

        Vector vz = new DenseVector(1, 1, 1);
        assertEquals(3, vectorMathOperation.norm(vz, 1), 0);

        Vector v2 = new DenseVector(1, 2, 3, 4, 5, Double.NaN, 7, 8, 9, 10);
        assertEquals(Double.NaN, vectorMathOperation.norm(v2, 3), 0);
        assertEquals(Arrays.stream(v1.toArray()).map(Math::abs).max().getAsDouble(), vectorMathOperation.norm(v1, Integer.MAX_VALUE), 0);
        assertEquals(Arrays.stream(v1.toArray()).map(Math::abs).min().getAsDouble(), vectorMathOperation.norm(v1, Integer.MIN_VALUE), 0);
        assertEquals(0d, vectorMathOperation.norm(new DenseVector(), 2), 0d);
    }

}