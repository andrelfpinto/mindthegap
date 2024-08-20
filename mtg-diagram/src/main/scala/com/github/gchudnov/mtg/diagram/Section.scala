package com.github.gchudnov.mtg.diagram

import com.github.gchudnov.mtg.Interval

/**
 * Section
 */
final case class Section[T](
  title: String,
  intervals: List[Interval[T]],
  annotations: List[String],
)
