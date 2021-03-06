/*
 * Copyright (c) Numerical Method Inc.
 * http://www.numericalmethod.com/
 * 
 * THIS SOFTWARE IS LICENSED, NOT SOLD.
 * 
 * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
 * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
 * DO NOT USE THIS SOFTWARE.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
 * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
 * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.numericalmethod.suanshu.stats.test.distribution.pearson;

import com.numericalmethod.suanshu.matrix.doubles.ImmutableMatrix;
import com.numericalmethod.suanshu.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.matrix.doubles.matrixtype.dense.DenseMatrix;
import static com.numericalmethod.suanshu.number.doublearray.DoubleArrayMath.sum;
import static com.numericalmethod.suanshu.misc.SuanShuUtils.assertArgument;
import com.numericalmethod.suanshu.stats.distribution.ProbabilityMassFunction;
import com.numericalmethod.suanshu.stats.random.univariate.RandomLongGenerator;
import com.numericalmethod.suanshu.stats.sampling.discrete.DiscreteSampling;
import static com.numericalmethod.suanshu.matrix.doubles.operation.MatrixUtils.*;
import com.numericalmethod.suanshu.number.DoubleUtils;
import com.numericalmethod.suanshu.stats.random.univariate.uniform.UniformRng;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Algorithm AS 159 accepts a table shape (the number of rows and columns), and two vectors, the lists of row and column sums.
 * There may be 0, 1, or many tables with nonnegative, integral entries that have the given shape and sums.
 * If there is at least one candidate, then the routine will choose one, uniformly over the number of distinct candidates.
 * The routine will report the case if there are no candidates.
 *
 * <p>
 * Other implementations includes 'rcont2'. For example,
 * <ul>
 * <li>http://svn.r-project.org/R/trunk/src/appl/rcont.c
 * <li>http://people.sc.fsu.edu/~jburkardt/cpp_src/asa159/asa159.C
 * <li>http://lib.stat.cmu.edu/apstat/159
 * </ul>
 *
 * @author Haksun Li
 *
 * @see "WM Patefield. "Algorithm AS 159: An Efficient Method of Generating RXC Tables with Given Row and Column Totals," Applied Statistics, Volume 30, Number 1, 1981, pages 91-97."
 */
public class AS159 implements Serializable{

    private static final long serialVersionUID = 3416509412429991833L;

    /**
     * a random matrix generated by AS159 and its probability
     */
    public static class RandomMatrix implements Serializable{

        private static final long serialVersionUID = -8850322632686223912L;
        /**
         * a random matrix constructed
         */
        public final ImmutableMatrix A;
        /**
         * the probability of observing this matrix
         */
        public final double prob;

        public RandomMatrix(Matrix A, double prob) {
            this.A = new ImmutableMatrix(A);
            this.prob = prob;
        }
    }
    private final RandomLongGenerator rng;
    private final int[] rowSums;
    private final int[] colSums;
    private final int N;
    private final double[] logFactorials;

    /**
     * Construct a random table generator according to row and column totals.
     * 
     * @param rowSums row totals
     * @param colSums column totals
     */
    public AS159(int[] rowSums, int[] colSums) {
        this(rowSums, colSums, new UniformRng());
    }

    /**
     * Construct a random table generator according to row and column totals.
     * 
     * @param rowSums row totals
     * @param colSums column totals
     * @param rng a uniform random number generator
     */
    public AS159(int[] rowSums, int[] colSums, RandomLongGenerator rng) {
        this.rng = rng;

        assertArgument(rowSums.length > 1, "the contingency table must have at least 2 rows");
        assertArgument(colSums.length > 1, "the contingency table must have at least 2 columns");

        for (int i = 0; i < rowSums.length; ++i) {
            assertArgument(rowSums[i] > 0, "a row sum must be +ve");
        }

        for (int i = 0; i < colSums.length; ++i) {
            assertArgument(colSums[i] > 0, "a column sum must be +ve");
        }

        int N1 = sum(rowSums);
        int N2 = sum(colSums);
        assertArgument(N1 == N2, "the row sum and the columns sum do not add up equal");
        this.N = N1;

        //compute the log-factorials
        this.logFactorials = new double[this.N + 1];
        this.logFactorials[0] = 0;

        double logSum = 0;
        for (int i = 1; i <= this.N; ++i) {
            logSum += Math.log(i);
            this.logFactorials[i] = logSum;
        }

        this.rowSums = new int[rowSums.length + 1];//our matrix/vector implementation counts from 1
        this.rowSums[0] = 0;//prepend a 0 in the front
        for (int i = 0; i < rowSums.length; ++i) {
            this.rowSums[i + 1] = rowSums[i];
        }

        this.colSums = new int[colSums.length + 1];//our matrix/vector implementation counts from 1
        this.colSums[0] = 0;//prepend a 0 in the front
        for (int i = 0; i < colSums.length; ++i) {
            this.colSums[i + 1] = colSums[i];
        }
    }

    /**
     * Check whether a matrix satisfies the row and column sums.
     * 
     * @param A a matrix
     * @return {@code true} if A satisfies the constraints
     */
    public boolean isValidated(Matrix A) {
        for (int i = 1; i <= A.nRows(); ++i) {//check entries
            for (int j = 1; j <= A.nCols(); ++j) {
                if (DoubleUtils.isNegative(A.get(i, j), 0)) {
                    return false;
                }
            }
        }

        int[] sum = rowSums(A);
        for (int i = 1; i <= A.nRows(); ++i) {//check row sums
            if (sum[i - 1] != rowSums[i]) {
                return false;
            }
        }

        sum = colSums(A);
        for (int i = 1; i <= A.nCols(); ++i) {//check column sums
            if (sum[i - 1] != colSums[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Construct a random matrix based on the row and column sums.
     *
     * @return a random matrix
     */
    public RandomMatrix nextSample() {
        //initialization
        int nRows = rowSums.length - 1;
        int nCols = colSums.length - 1;
        DenseMatrix A = new DenseMatrix(nRows, nCols);

        double prob = 1;

        /**
         * a_.m - ?? {i=1:l-1} a_im, a factor in the numerator in the 2nd equation
         */
        int[] remainingColSums = Arrays.copyOf(colSums, colSums.length);//the column sum left for a column to fill the (l, m) entry

        for (int l = 1; l < nRows; ++l) {//we fill the matrix entries by rows, up to the second to last row
            /**
             * a_l. - ?? {j=1:m-1} a_lj, a factor in the numerator in the 2nd equation
             */
            int remainingRowSum_l = rowSums[l];//the sum to fill out for the rest of row l

            for (int m = 1; m < nCols; ++m) {//the last column can be computed from the remainder
                /**
                 * 'big' is the sum of the sub-matrix with the (1, 1) entry being the (l, m) entry in M.
                 * E.g., for (3, 3)
                 * 1   2    3  4  5
                 * 6   7    8  9 10
                 *       -----------
                 * 11 12 | 13 14 15
                 * 16 17 | 18 19 20
                 * 21 22 | 23 24 25
                 *
                 * ?? {i=m:c} (a_.j - ?? {i=1:l-1} a_ij), the denominator in the 2nd equation
                 */
                int big = 0;
                for (int i = m; i <= nCols; ++i) {
                    big += remainingColSums[i];
                }

                /**
                 * 'right' is the sum of the sub-matrix with the (1, 1) entry being the (l, m+1) entry in M.
                 * E.g., for (3, 3)
                 * 1   2  3    4  5
                 * 6   7  8    9 10
                 *          -------
                 * 11 12 13 | 14 15
                 * 16 17 18 | 19 20
                 * 21 22 23 | 24 25
                 */
                int right = big - remainingColSums[m];

                /**
                 * 'below' is the sum of the sub-matrix with the (1, 1) entry being the (l+1, m) entry in M.
                 * E.g., for (3, 3)
                 * 1   2    3  4  5
                 * 6   7    8  9 10
                 * 11 12   13 14 15
                 *        ----------
                 * 16 17 | 18 19 20
                 * 21 22 | 23 24 25
                 */
                int below = big - remainingRowSum_l;

                /**
                 * 'small' is the sum of the sub-matrix with the (1, 1) entry being the (l+1, m+1) entry in M *minus* a_lm.
                 * E.g., for (3, 3)
                 * 1   2  3    4  5
                 * 6   7  8    9 10
                 * 11 12 13   14 15
                 *           -------
                 * 16 17 18 | 19 20
                 * 21 22 23 | 24 25
                 */
                int small = big - remainingColSums[m] - remainingRowSum_l;

                //row A[l,] is full, fill rest with zero entries
                if (big == 0) {
                    for (int i = m; i <= nCols; ++i) {
                        A.set(l, i, 0);
                    }

                    break;//proceed to the next row
                }

                //nextSample a random entry for a_lm using the conditional probability mass, starting with the expected value
                ConditionalDistribution dist = new ConditionalDistribution(remainingRowSum_l, remainingColSums[m], big, small, right, below);
                DiscreteSampling<Integer> sampler = new DiscreteSampling<Integer>(dist, dist);
                Integer a_lm = null;
                for (; a_lm == null;) {
                    a_lm = sampler.getSample(rng.nextDouble());
                }

                //found a_lm
                A.set(l, m, a_lm);
                remainingRowSum_l -= a_lm;
                remainingColSums[m] -= a_lm;

                prob *= dist.evaluate(a_lm);//eq. 2
            }

            //fill the last column
            A.set(l, nCols, remainingRowSum_l);
            remainingColSums[nCols] -= remainingRowSum_l;
        }

        //fill out the last row
        for (int i = 1; i <= nCols; ++i) {
            A.set(nRows, i, remainingColSums[i]);
        }

        return new RandomMatrix(A, prob);
    }

    private class ConditionalDistribution implements Iterable<Integer>, ProbabilityMassFunction<Integer> {

        private static final long serialVersionUID = -1862148959111209982L;
        private final int remainingRowSum_l;
        private final int remainingColSums_m;
        private final int small;
        private final int Elm;
        private final double Plm;
        private int last = 0;
        private double last_Plm;
        private int inc_lm = 0;
        private double inc_Plm;
        private int dec_lm = 0;
        private double dec_Plm;

        private ConditionalDistribution(int remainingRowSum_l, int remainingColSums_m, int big, int small, int right, int below) {
            this.remainingRowSum_l = remainingRowSum_l;
            this.remainingColSums_m = remainingColSums_m;
            this.small = small;

            this.Elm = Math.round((float) remainingRowSum_l * remainingColSums_m / big);//expected value of a_lm
            //compute the condition probability according to eq. 1
            this.Plm = Math.exp(
                    logFactorials[remainingRowSum_l]
                    + logFactorials[below]
                    + logFactorials[remainingColSums_m]
                    + logFactorials[right]
                    - logFactorials[Elm]
                    - logFactorials[remainingColSums_m - Elm]
                    - logFactorials[remainingRowSum_l - Elm]
                    - logFactorials[small + Elm]
                    - logFactorials[big]);

            this.inc_lm = this.Elm;
            this.dec_lm = this.Elm;
            this.inc_Plm = this.Plm;
            this.dec_Plm = this.Plm;
        }

        public Iterator<Integer> iterator() {
            return new Iterator<Integer>() {

                public boolean hasNext() {
                    if (inc_lm < 0 && dec_lm < 0) {
                        return false;
                    }

                    return true;
                }

                public Integer next() {
                    if (last == 0) {
                        last = Elm;
                        last_Plm = Plm;

                        inc();
                        dec();
                    } else if (inc_lm >= 0 && (last <= Elm || dec_lm < 0)) {
                        last = inc_lm;
                        last_Plm = inc_Plm;

                        inc();
                    } else if (dec_lm >= 0 && (last > Elm || inc_lm < 0)) {
                        last = dec_lm;
                        last_Plm = dec_Plm;

                        dec();
                    }

                    return last;
                }

                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            };
        }

        public double evaluate(Integer x) {//return the conditional probability
            if (x == last) {
                return last_Plm;
            }

            throw new RuntimeException("this probability mass function must be called in order");
        }

        private void inc() {
            double p = (remainingColSums_m - inc_lm) * (remainingRowSum_l - inc_lm);
            if (p > 0) {
                ++inc_lm;
                inc_Plm *= p / ((double) inc_lm * (small + inc_lm));
            } else {
                inc_lm = -1;//no more right candidates
            }
        }

        private void dec() {
            double q = dec_lm * (small + dec_lm);
            if (q > 0) {
                --dec_lm;
                dec_Plm *= q / ((double) (remainingColSums_m - dec_lm) * (remainingRowSum_l - dec_lm));
            } else {
                dec_lm = -1;//no more left candidates
            }
        }
    }
}
