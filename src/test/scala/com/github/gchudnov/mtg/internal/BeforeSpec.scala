package com.github.gchudnov.mtg.internal

import com.github.gchudnov.mtg.Arbitraries.*
import com.github.gchudnov.mtg.Interval
import com.github.gchudnov.mtg.TestSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.*

/**
 * Before, After (Preceeds, IsPreceededBy)
 *
 * {{{
 *   AAA
 *        BBB
 * }}}
 */
final class BeforeSpec extends TestSpec:

  given intRange: IntRange = intRange5
  given intProb: IntProb   = intProb127

  given config: PropertyCheckConfiguration = PropertyCheckConfiguration(maxDiscardedFactor = 1000.0)

  "Before" when {
    "before (preceeds) & after (isPreceededBy)" should {
      "auto check" in {
        import IntervalRelAssert.*

        forAll(genOneOfIntArgs, genOneOfIntArgs) { case (((ox1, ix1), (ox2, ix2)), ((oy1, iy1), (oy2, iy2))) =>
          val xx = Interval.make(ox1, ix1, ox2, ix2)
          val yy = Interval.make(oy1, iy1, oy2, iy2)

          whenever(xx.before(yy)) {
            assertOne(Rel.Before)(xx, yy)
          }
        }
      }

      "manual check" in {
        // Empty
        Interval.empty[Int].before(Interval.empty[Int]) mustBe (false)
        Interval.empty[Int].before(Interval.point(1)) mustBe (false)
        Interval.empty[Int].before(Interval.closed(1, 4)) mustBe (false)
        Interval.empty[Int].before(Interval.open(1, 4)) mustBe (false)
        Interval.empty[Int].before(Interval.unbounded[Int]) mustBe (false)

        // Point
        Interval.point(5).before(Interval.empty[Int]) mustBe (false)
        Interval.point(5).before(Interval.point(5)) mustBe (false)
        Interval.point(5).before(Interval.point(6)) mustBe (true)
        Interval.point(6).after(Interval.point(5)) mustBe (true)
        Interval.point(5).before(Interval.point(10)) mustBe (true)
        Interval.point(5).before(Interval.open(5, 10)) mustBe (true)
        Interval.point(5).before(Interval.closed(5, 10)) mustBe (false)
        Interval.point(5).before(Interval.closed(6, 10)) mustBe (true)
        Interval.point(5).before(Interval.leftClosed(5)) mustBe (false)
        Interval.point(5).before(Interval.leftClosed(6)) mustBe (true)
        Interval.point(5).before(Interval.unbounded[Int]) mustBe (false)

        // [-∞,0], {4}
        Interval.point(4).after(Interval.proper[Int](None, true, Some(0), true)) mustBe (true)
        Interval.proper[Int](None, true, Some(0), true).before(Interval.point(4)) mustBe (true)

        // Proper
        Interval.open(4, 7).before(Interval.open(4, 7)) mustBe (false)
        Interval.open(1, 4).before(Interval.empty[Int]) mustBe (false)
        Interval.open(1, 4).before(Interval.open(5, 8)) mustBe (true)
        Interval.open(5, 8).after(Interval.open(1, 4)) mustBe (true)
        Interval.open(1, 4).before(Interval.closed(5, 6)) mustBe (true)

        Interval.open(1, 4).before(Interval.open(3, 6)) mustBe (true)
        Interval.open(3, 6).after(Interval.open(1, 4)) mustBe (true)

        // Infinity
        // (1, 4)  (3, +inf)
        Interval.open(1, 4).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  (3, 6)
        Interval.rightOpen(2).before(Interval.open(3, 6)) mustBe (true)

        // (-inf, 2)  (3, +inf)
        Interval.rightOpen(2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2]  (3, +inf)
        Interval.rightClosed(2).before(Interval.leftOpen(3)) mustBe (true)

        // (-inf, 2)  [3, +inf)
        Interval.rightOpen(2).before(Interval.leftClosed(3)) mustBe (true)
      }
    }
  }
