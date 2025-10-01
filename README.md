There are three types of variance annotations in Scala:

1. Covariance (+)

```scala
trait Tag[+El <: ReactiveElement]
```

Meaning: If `A` is a subtype of `B`, then `Tag[A]` is a subtype of `Tag[B]`
Use case: When the type parameter is only used in "output" positions (return types)
Example: `List[+A]` - you can assign `List[Dog]` to `List[Animal]`

2. Contravariance (-)

```scala
trait Processor[-El <: ReactiveElement]
```

Meaning: If `A` is a subtype of `B`, then `Processor[B]` is a subtype of `Processor[A]` (reversed!)
Use case: When the type parameter is only used in "input" positions (method parameters)
Example: `Function1[-T, +R]` - a function that accepts `Animal` can be used where `Dog` is expected

3. Invariance (no annotation)

```scala
trait Container[El <: ReactiveElement]
```

Meaning: No subtype relationship - `Container[A]` and `Container[B]` are unrelated even if `A <: B`
Use case: When the type is used in both input and output positions (mutable collections)
Example: `Array[T]` - you cannot assign `Array[Dog]` to `Array[Animal]`

Quick comparison:

```scala
// Covariant (+): subtype relationship preserved
List[Dog] <: List[Animal] ✓

// Contravariant (-): subtype relationship reversed  
Processor[Animal] <: Processor[Dog] ✓

// Invariant (none): no relationship
Array[Dog] and Array[Animal] are unrelated
```