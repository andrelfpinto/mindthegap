+++
title = "Intervals"
description = "Intervals"
date = 2024-09-03
draft = false

[extra]
+++

## Intervals

An **interval** defined by a pair `{a-, a+}`, that represent a starting `a-` and ending `a+` values on a discrete linear infinite domain `D`.
Interval is a _convex_ set that contains all elements between `a-` and `a+`.

![domain.svg](./domain.svg)

For each element of the domain `D`, there is only one _successor_ and _predecessor_ and no other elements in-between.

A pair `{a-, a+} ∈ D × D` corresponds to a point on a two-dimensional plane.

Given _successor_, `succ()` and _predecessor_, `pred()` functions that allow to get the next and the previous element, we can define different types of intervals as closed intervals:

- closed interval `[a-, a+]` is represented as-is `[a-, a+]`
- right-open interval`[a-, a+)` is represented as `[a-, pred(a+)]`
- left-open interval `(a-, a+]` is represented as `[succ(a-), a+]`
- open interval `(a-, a+)` is represented as `[succ(a-), pred(a+)]`

We consider an interval to be in a _canonical_ form when it is represented as a closed interval.

## Classification

A pair `{a-, a+}` represents a _non-empty_ interval if `a- ≤ a+`; otherwise, the interval is _empty_.
If left boundary `a-` is equal to the right boundary, `a+` (consists of a single value only), we call it as a _degenerate_ interval or a _point_.
A non-empty interval is _proper_ if left boundary is less than the right boundary `a- < a+`.

A _proper_ interval is _bounded_, if it is both left- and right-bounded; and _unbounded_ otherwise.

On the diagram above, _proper_ intervals represented as points _above_ the line `a+ = a-`, _point_ intervals
are located on the line `a+ = a-` and all empty intervals are _below_ the line `a+ = a-`.

- empty : `a- > a+`
- point : `{x} = {x | a- = x = a+}`
- proper : `a- < a+`
  - bounded
    - open : `(a-, a+) = {x | a- < x < a+}`
    - closed : `[a-, a+] = {x | a- <= x <= a+}`
    - left-closed, right-open : `[a-, a+) = {x | a- <= x < a+}`
    - left-open, right-closed : `(a-, a+] = {x | a- < x <= a+}`
  - left-bounded, right-unbounded
    - left-open : `(a-, +∞) = {x | x > a-}`
    - left-closed : `[a-, +∞) = {x | x >= a-}`
  - left-unbounded, right-bounded
    - right-open : `(-∞, a+) = {x | x < a+}`
    - right-closed : `(-∞, a+] = {x | x < a+}`
  - unbounded : `(-∞, +∞)`

## Creation

To create an interval one of the factory methods can be used:

```scala
import com.github.gchudnov.mtg.*

Interval.empty[Int]                 // ∅ = (+∞, -∞)
Interval.point(5)                   // {5}
Interval.open(1, 5)                 // (1, 5)
Interval.closed(1, 5)               // [1, 5]
Interval.leftClosedRightOpen(1, 5)  // [1, 5)
Interval.leftOpenRightClosed(1, 5)  // (1, 5]
Interval.leftOpen(1)                // (1, +∞)
Interval.leftClosed(5)              // [5, +∞)
Interval.rightOpen(1)               // (-∞, 1)
Interval.rightClosed(5)             // (-∞, 5]
Interval.unbounded[Int]             // (-∞, +∞)

Interval.open(Some(1), Some(5))     // (1, 5)
Interval.open(Some(1), None)        // (1, +∞)
Interval.open(None, Some(5))        // (-∞, 5)
Interval.open(None, None)           // (-∞, +∞)

Interval.closed(Some(1), Some(5))   // [1, 5]
Interval.closed(Some(1), None)      // [1, +∞)
Interval.closed(None, Some(5))      // (-∞, 5]
Interval.closed(None, None)         // (-∞, +∞)
```

## Operations

Given an interval `a`,

`a.isEmpty`, `a.isPoint`, `a.isProper` (`a.nonEmpty`, `a.nonPoint`, `a.nonProper`) can be used to check the type of an interval.

```scala
Interval.open(1, 5).isEmpty  // false
Interval.open(1, 5).isProper // true
Interval.open(1, 5).isPoint  // false
```

### Canonical

`a.canonical` produces the canonical form of an interval where left and right boundaries are _closed_.

A _canonical_ form of a closed interval is the interval itself.

```scala
Interval.open(1, 5).canonical   // (1, 5) -> [2, 4]
Interval.closed(1, 5).canonical // [1, 5] -> [1, 5]
```

### Swap

`a.swap` swaps left and right boundary, to convert a _non-empty_ interval to an _empty_ interval or vice versa.

```scala
Interval.closed(1, 5).swap // [1, 5] -> [5, 1]
```

![swap.svg](./swap.svg)

### Inflate

`a.inflate` inflates an interval, extending its size: `[a-, a+] -> [pred(a-), succ(a+)]`.

```scala
Interval.closed(1, 2).inflate // [1, 2] -> [0, 3]
```

![inflate.svg](./inflate.svg)

In addition, `a.inflateLeft` and `a.inflateRight` methods extend left and right boundaries of an interval.

### Deflate

`a.deflate` deflates an interval, reducing its size: `[a-, a+] -> [succ(a-), pred(a+)]`.

NOTE: it is possible that after deflation an interval becomes _empty_.

```scala
Interval.closed(1, 2).deflate // [1, 2] -> [2, 1]
```

![deflate.svg](./deflate.svg)

In addition, `a.deflateLeft` and `a.deflateRight` methods shrink left and right boundaries of an interval.

## Show

Use `.toString` to represent an interval in a human-readable form:

```scala
val a = Interval.empty[Int]
val b = Interval.point(5)
val c = Interval.proper(None, true, Some(2), false)

a.toString // ∅
b.toString // {5}
c.toString // [-∞,2)
```

## Display

A collection of intervals can be displayed, using ASCII or [Mermaid](https://mermaid.js.org/) diagrams.

### ASCII

```scala
import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*

val a = Interval.closed(3, 7)
val b = Interval.closed(10, 15)
val c = Interval.closed(12, 20)

val renderer = AsciiRenderer.make[Int]()
val diagram = Diagram
  .empty[Int]
  .withSection { s =>
    List(a, b, c).zipWithIndex.foldLeft(s) { case (s, (i, k)) =>
      s.addInterval(i, s"${('a' + k).toChar}")
    }
  }

renderer.render(diagram)

println(renderer.result)
```

Produces the following output:

```text
  [*******]                              | [3,7]   : a
                [**********]             | [10,15] : b
                     [***************]   | [12,20] : c
--+-------+-----+----+-----+---------+-- |
  3       7    10   12    15        20   |
```

Canvas size and theme can be customized. See [examples directory](https://github.com/gchudnov/mindthegap/tree/main/examples/src/main/scala/com/github/gchudnov/examples) for more details.

### Mermaid

Only Date/Time intervals are supported for Mermaid diagrams.

```scala
import com.github.gchudnov.mtg.*
import com.github.gchudnov.mtg.diagram.*
import java.time.*

val t1 = LocalTime.parse("04:00")
val t2 = LocalTime.parse("10:00")
val t3 = LocalTime.parse("08:00")
val t4 = LocalTime.parse("20:00")

val a = Interval.closed(t1, t2)
val b = Interval.closed(t3, t4)

val renderer = MermaidRenderer.make[LocalTime]
val diagram = Diagram
  .empty[LocalTime]
  .withTitle("My Mermaid Diagram")
  .withSection { s =>
    val s0 = s.withTitle("My Section")
    List(a, b).zipWithIndex.foldLeft(s0) { case (s, (i, k)) =>
      s.addInterval(i, s"${('a' + k).toChar}")
    }
  }

renderer.render(diagram)

println(renderer.result)
```

That produces the mermaid diagram:

```mermaid
gantt
  title       My Mermaid Diagram
  dateFormat  HH:mm:ss.SSS
  axisFormat  %H:%M:%S

  section My Section
  a  :04:00:00.000, 10:00:00.000
  b  :08:00:00.000, 20:00:00.000
```

It can be rendered using the [Mermaid Live Editor](https://mermaid.live/).

## Domain

To work with intervals, a `given` instance of `Domain[T]` is needed. It is provided by default for _integral_ and _date-type_ types.

A custom domain can be defined ([example](https://github.com/gchudnov/mindthegap/blob/main/examples/src/main/scala/com/github/gchudnov/examples/CustomCharDomain.scala)) for a specific type or constructed using family of make functions ([example](https://github.com/gchudnov/mindthegap/blob/main/examples/src/main/scala/com/github/gchudnov/examples/CustomOffsetDateTimeDomain.scala)).

## Ordering

Intervals can be ordered:

- if `a- < b+` then `a < b`
- if `a- == b+` then
  - if `a+ < b+` then `a < b`
  - if `a+ == b+` then `a == b`
  - else `a > b`
- else `a > b`

```scala
val a = Interval.closed(0, 10)   // [0, 10]
val b = Interval.closed(20, 30)  // [20, 30]

List(b, a).sorted // List(a, b)  // [0, 10], [20, 30]
```
