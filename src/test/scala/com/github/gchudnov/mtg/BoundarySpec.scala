package com.github.gchudnov.mtg

import com.github.gchudnov.mtg.TestSpec
import com.github.gchudnov.mtg.*

import java.time.Instant
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

final class BoundarySpec extends TestSpec:

  given domainDouble: Domain[Double]                 = Domain.makeFractional[Double](0.01)
  given domainOffsetDateTime: Domain[OffsetDateTime] = Domain.makeOffsetDateTime(ChronoUnit.MINUTES)
  given domainInstant: Domain[Instant]               = Domain.makeInstant(ChronoUnit.MINUTES)

  "Boundary" when {

    "sorted" should {
      "order the boundaries" in {
        val bs = List(
          Boundary.Left(Some(1), false),
          Boundary.Left(Some(1), true),
          Boundary.Right(Some(4), false),
          Boundary.Right(Some(4), true)
        )

        val expected = List(
          Boundary.Left(Some(1), true),
          Boundary.Left(Some(1), false),
          Boundary.Right(Some(4), false),
          Boundary.Right(Some(4), true)
        )

        bs.sorted mustBe expected
      }
    }

    "Boundary.Left" should {
      "check an effective value of an Int" in {
        Boundary.Left[Int](None, true).effectiveValue mustBe (None)
        Boundary.Left[Int](None, false).effectiveValue mustBe (None)
        Boundary.Left[Int](Some(1), true).effectiveValue mustBe (Some(1))
        Boundary.Left[Int](Some(1), false).effectiveValue mustBe (Some(2))
      }

      "check an effective value of an Double" in {
        Boundary.Left[Double](None, true).effectiveValue mustBe (None)
        Boundary.Left[Double](None, false).effectiveValue mustBe (None)
        Boundary.Left[Double](Some(1.0), true).effectiveValue mustBe (Some(1.0))
        Boundary.Left[Double](Some(1.0), false).effectiveValue mustBe (Some(1.01))
      }

      "check an effective value of an OffsetDateTime" in {
        Boundary.Left[OffsetDateTime](None, true).effectiveValue mustBe (None)
        Boundary.Left[OffsetDateTime](None, false).effectiveValue mustBe (None)
        Boundary.Left[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), true).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        Boundary.Left[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), false).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:35Z")))
      }

      "check an effective value of an Instant" in {
        Boundary.Left[Instant](None, true).effectiveValue mustBe (None)
        Boundary.Left[Instant](None, false).effectiveValue mustBe (None)
        Boundary.Left[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), true).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:34:00Z")))
        Boundary.Left[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), false).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:35:00Z")))
      }
    }

    "Boundary.Right" should {
      "check an effective value of an Int" in {
        Boundary.Right[Int](None, true).effectiveValue mustBe (None)
        Boundary.Right[Int](None, false).effectiveValue mustBe (None)
        Boundary.Right[Int](Some(1), true).effectiveValue mustBe (Some(1))
        Boundary.Right[Int](Some(1), false).effectiveValue mustBe (Some(0))
      }

      "check an effective value of an Double" in {
        Boundary.Right[Double](None, true).effectiveValue mustBe (None)
        Boundary.Right[Double](None, false).effectiveValue mustBe (None)
        Boundary.Right[Double](Some(1.0), true).effectiveValue mustBe (Some(1.0))
        Boundary.Right[Double](Some(1.0), false).effectiveValue mustBe (Some(0.99))
      }

      "check an effective value of an OffsetDateTime" in {
        Boundary.Right[OffsetDateTime](None, true).effectiveValue mustBe (None)
        Boundary.Right[OffsetDateTime](None, false).effectiveValue mustBe (None)
        Boundary.Right[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), true).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:34Z")))
        Boundary.Right[OffsetDateTime](Some(OffsetDateTime.parse("2017-07-02T12:34Z")), false).effectiveValue mustBe (Some(OffsetDateTime.parse("2017-07-02T12:33Z")))
      }

      "check an effective value of an Instant" in {
        Boundary.Right[Instant](None, true).effectiveValue mustBe (None)
        Boundary.Right[Instant](None, false).effectiveValue mustBe (None)
        Boundary.Right[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), true).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:34:00Z")))
        Boundary.Right[Instant](Some(Instant.parse("2017-07-02T12:34:00Z")), false).effectiveValue mustBe (Some(Instant.parse("2017-07-02T12:33:00Z")))
      }
    }
  }
