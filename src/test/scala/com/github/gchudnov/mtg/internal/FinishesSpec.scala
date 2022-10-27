package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Boundary
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Finishes, IsFinishedBy
 *
 * {{{
 *      AAA
 *   BBBBBB
 * }}}
 */
final class FinishesSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  val ordB: Ordering[Boundary[Int]] = summon[Ordering[Boundary[Int]]]

  "Finishes" when {
    import IntervalRelAssert.*

    "a.finishes(b)" should {
      "b.finisedBy(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.finishes(yy)) {
            yy.isFinishedBy(xx) mustBe true

            assertOne(Rel.Finishes)(xx, yy)

            // a+ = b+ && b.isSuperset(a) && !a.equalsTo(b)
            val a2 = Boundary.Right(ox2, ix2)
            val b2 = Boundary.Right(oy2, iy2)

            (ordB.equiv(a2, b2) && yy.isSuperset(xx) && !xx.equalsTo(yy)) mustBe true
          }
        }
      }
    }

    "a.finisedBy(b)" should {
      "b.finishes(a)" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.isFinishedBy(yy)) {
            yy.finishes(xx) mustBe true

            assertOne(Rel.IsFinishedBy)(xx, yy)

            // a+ = b+ && a.isSuperset(b) && !a.equalsTo(b)
            val a2 = Boundary.Right(ox2, ix2)
            val b2 = Boundary.Right(oy2, iy2)

            (ordB.equiv(a2, b2) && xx.isSuperset(yy) && !xx.equalsTo(yy)) mustBe true
          }
        }
      }
    }

    "a.finishes(b) AND b.isFinishedBy(a)" should {

      "equal" in {
        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          val actual   = xx.finishes(yy)
          val expected = yy.isFinishedBy(xx)

          actual mustBe expected
        }
      }

      "valid in special cases" in {
        // Empty
        Interval.empty[Int].finishes(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].finishes(Interval.point(0)) mustBe (false)
        Interval.empty[Int].finishes(Interval.closed(0, 5)) mustBe (false)

        // Point
        Interval.point(5).finishes(Interval.empty[Int]) mustBe (false)
        Interval.point(5).finishes(Interval.point(5)) mustBe (false)
        Interval.point(5).finishes(Interval.closed(1, 5)) mustBe (true)
        Interval.point(1).finishes(Interval.closed(1, 5)) mustBe (false)

        // Proper
        // [0,5)  [-1,5)
        Interval.leftClosedRightOpen(0, 5).finishes(Interval.leftClosedRightOpen(-1, 5)) mustBe (true)
        Interval.leftClosedRightOpen(-1, 5).isFinishedBy(Interval.leftClosedRightOpen(0, 5)) mustBe (true)

        // (5, 10]  (2, 10]
        Interval.leftOpenRightClosed(5, 10).finishes(Interval.leftOpenRightClosed(2, 10)) mustBe (true)

        // Infinity
        // [5, 10]  (-inf, 10]
        Interval.closed(5, 10).finishes(Interval.rightClosed(10)) mustBe (true)

        // [10, +inf)  [5, +inf)
        Interval.leftClosed(10).finishes(Interval.leftClosed(5)) mustBe (true)

        // [5, +inf)  (-inf, +inf)
        Interval.leftClosed(5).finishes(Interval.unbounded[Int]) mustBe (true)

        // (0, 3)  (-inf, 3)
        Interval.open(0, 3).finishes(Interval.rightOpen(3)) mustBe (true)

        // (-inf, +inf)  (-inf, +inf)
        Interval.unbounded[Int].finishes(Interval.unbounded[Int]) mustBe (false)

        // (-inf, 2]  [-inf, 3)
        Interval.make[Int](None, false, Some(2), true).finishes(Interval.make[Int](None, true, Some(3), false))
        ordB.equiv(Boundary.Right(Some(2), true), Boundary.Right(Some(3), false)) mustBe true
      }
    }
  }
