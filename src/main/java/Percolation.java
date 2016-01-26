
/**
 * Percolation system of size N x N.
 *  
 * @author roberto.zagni - Copyright (c) 2016
 */

import static org.junit.Assert.assertEquals;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * This class models a Percolation system of size N x N.
 * 
 * @author roberto.zagni - Copyright (c) 2016
 */
public class Percolation {

  /** The number of row and columns. The number of locations is N*N. */
  private int N;

  /** The index of node representing the TOP where percolation starts. */
  private final int theTOP;

  /** The index of node representing the BOTTOM where percolation ends. */
  private final int theBOTTOM;

  // /** The value telling if the system percolates or not. */
  // private boolean percolates;

  /** The array tracking the sites that are open. */
  private boolean[] open = null;

  /** The union-find ADT tracking the connected sites. */
  private WeightedQuickUnionUF uf = null;

  /**
   * create N-by-N grid, with all sites blocked.
   * 
   * @param N the size of the grid.
   * @throws IllegalArgumentException if N ≤ 0.
   */
  public Percolation(int N) {
    if (N <= 0) {
      throw new IllegalArgumentException("Provided size (" + N + ") is not positive.");
    }
    if (Integer.MAX_VALUE / N < N) {
      throw new IllegalArgumentException("Provided size (" + N + ") is too big for N*N to be indexed by integers.");
    }
    this.N = N;
    this.open = new boolean[N * N];
    this.uf = new WeightedQuickUnionUF(N * N + 2);
    this.theTOP = N * N;
    this.theBOTTOM = this.theTOP + 1;
  }

  /**
   * The linearized 0-based index from the two 1-based dimensions.
   * 
   * @param row the 1 based dimension for rows;
   * @param col the 1 based dimension for columns;
   * @return the 0-based 1D index corresponding to the given 2D position
   */
  private int index(int row, int col) {
    return (row - 1) * N + (col - 1) * 1;
  }

  /**
   * Checks that the given value is in the range 1 to N.
   * 
   * @param val the value to be validated
   * @throws IndexOutOfBoundsException if value is out of range.
   */
  private void validate(int val) {
    if (val <= 0 || val > N) {
      throw new IndexOutOfBoundsException(String.format("Value %d is out of bounds ( 1 to %d).", val, N));
    }
  }

  // open site (row i, column j) if it is not open already
  public void open(int row, int col) {
    validate(row);
    validate(col);
    if (!isOpenV(row, col)) {

      // open it
      int idx = index(row, col);
      open[idx] = true;

      // connect UP
      if (row == 1) { // if row = 1 connect to TOP
        uf.union(idx, theTOP);
      } else if (isOpenV(row - 1, col)) { // if UP isOpen connect to it
        uf.union(idx, index(row - 1, col));
      }

      // connect LEFT
      if (col > 1 && isOpenV(row, col - 1)) {
        uf.union(idx, index(row, col - 1));
      }

      // connect RIGHT
      if (col < N && isOpenV(row, col + 1)) {
        uf.union(idx, index(row, col + 1));
      }

      // connect DOWN
      if (row != N && isOpenV(row + 1, col)) { // if UP isOpen connect to it
        uf.union(idx, index(row + 1, col));
      }
    }
  }

  /**
   * Returns <code>true</code> if site (row i, column j) is open.
   * 
   * @param row the row in the grid (1 based).
   * @param col the column in the grid (1 based).
   */
  public boolean isOpen(int row, int col) {
    validate(row);
    validate(col);
    return isOpenV(row, col);
  }

  private boolean isOpenV(int row, int col) {
    return open[index(row, col)];
  }

  /**
   * Returns <code>true</code> if site (row i, column j) is full, i.e. open and connected with an open site in the top.
   * 
   * @param row the row in the grid (1 based).
   * @param col the column in the grid (1 based).
   */
  public boolean isFull(int row, int col) {
    validate(row);
    validate(col);
    return isFullV(row, col);
  }

  private boolean isFullV(int row, int col) {
    return isOpenV(row, col) && uf.connected(index(row, col), theTOP);
  }

  /**
   * Returns <code>true</code> if the system percolate, i.e. open and bottom are connected by open sites.
   * 
   * @param row the row in the grid (1 based).
   * @param col the column in the grid (1 based).
   */
  public boolean percolates() {
    for (int i = index(N, 1); i <= index(N, N); i++) {
      if (open[i] && uf.connected(i, theTOP)) {
        return true;
      }
    }
    return false;
  }

  // test client (optional)
  public static void main(String[] args) {
    System.out.println("Running tests...");
    testIndexing();
    PercolationTest test = new PercolationTest();
    test.atStrtupAllClosed();
    test.canAvoidBackFillOnPercolation();
    test.canCreateObjects();
    try {
      test.canNotCreateNis0();
    } catch (IllegalArgumentException e) {
      /* EXPECTED */ }
    try {
      test.canNotCreateNisNegative();
    } catch (IllegalArgumentException e) {
      /* EXPECTED */ }
    try {
      test.canNotCreateNisTooBig();
    } catch (IllegalArgumentException e) {
      /* EXPECTED */ }
    try {
      test.canNotCreateNisTooBig2();
    } catch (IllegalArgumentException e) {
      /* EXPECTED */ }
    test.percolateWhenJoinInMiddle();
    test.testIsFullInRange();
    test.testIsFullOutOfRange();
    test.testIsOpenOutOfRange();
    test.testOpenInRange();
    try {
      test.testOpenOutOfRangeLowCol();
    } catch (IndexOutOfBoundsException e) {
      /* EXPECTED */ }
    try {
      test.testOpenOutOfRangeLowRow();
    } catch (IndexOutOfBoundsException e) {
      /* EXPECTED */ }
    try {
      test.testOpenOutOfRangeUpCol();
    } catch (IndexOutOfBoundsException e) {
      /* EXPECTED */ }
    try {
      test.testOpenOutOfRangeUpRow();
    } catch (IndexOutOfBoundsException e) {
      /* EXPECTED */ }

    System.out.println("... done!");

  }

  private static void testIndexing() {
    Percolation p = new Percolation(10);
    assertEquals(0, p.index(1, 1));
    assertEquals(9, p.index(1, 10));
    assertEquals(19, p.index(2, 10));
    assertEquals(40, p.index(5, 1));
    assertEquals(49, p.index(5, 10));
    assertEquals(90, p.index(10, 1));
    assertEquals(99, p.index(10, 10));
  }

}
