package www

import com.raquo.laminar.api.L.*

case class App() {
  def apply(): HtmlElement = {
    div(
      cls("h-screen w-screen flex justify-center items-center"),
      "Hello, World!"
    )
  }
}

object App {
  /*
  Without the + (covariant), the assignment val baseTag: Tag[ReactiveElement.Base] = divTag would fail, and you couldn't pass a Tag[DivElement] to a function expecting Tag[ReactiveElement.Base].
The + makes the trait more flexible by preserving the subtype relationship through the type parameter.
  The key point: because of the + covariance, you can pass Tag[DivElement] or Tag[SpanElement] to a function expecting Tag[ReactiveElement], making the API much more flexible!
   */
  trait Tag[+El <: ReactiveElement] {

    val name: String

    val void: Boolean

    /** The string returned by `element.tagName` in JS DOM. It's uppercase for
      * HTML elements, and case-preserving for SVG elements.
      */
    def jsTagName: String
  }

  class ReactiveElement
  class DivElement extends ReactiveElement
  class SpanElement extends DivElement

  object DivTag extends Tag[DivElement] {
    val name: String = "div"
    val void: Boolean = false
    def jsTagName: String = "DIV"
  }

  object SpanTag extends Tag[SpanElement] {
    val name: String = "span"
    val void: Boolean = false
    def jsTagName: String = "SPAN"
  }

  val divTag: Tag[DivElement] = DivTag
  val spanTag: Tag[SpanElement] = SpanTag
  val generalTag1: Tag[DivElement] = spanTag // ✓ SpanElement <: DivElement
  val generalTag2: Tag[ReactiveElement] =
    divTag // ✓ DivElement <: ReactiveElement
  val generalTag3: Tag[ReactiveElement] =
    spanTag // ✓ SpanElement <: ReactiveElement

// Practical use case - function that accepts any tag:
  def processTags(tag: Tag[ReactiveElement]): Unit = {
    println(s"Processing tag: ${tag.name} (${tag.jsTagName})")
  }

  def run() = {
    processTags(DivTag) // ✓ Works!
    processTags(SpanTag) // ✓ Works!
  }
}

object Demo {
  trait Modifier[-El <: ReactiveElement] {
    def apply(element: El): Unit = ()
  }
  // Type hierarchy
  class ReactiveElement {
    var className: String = ""
    var id: String = ""
  }

  class DivElement extends ReactiveElement {
    var divSpecificProp: String = ""
  }

  class SpanElement extends DivElement {
    var spanSpecificProp: String = ""
  }

// Concrete modifier implementations
  object GeneralModifier extends Modifier[ReactiveElement] {
    override def apply(element: ReactiveElement): Unit = {
      element.className = "styled"
      println(s"Applied general styling to element")
    }
  }

  object DivModifier extends Modifier[DivElement] {
    override def apply(element: DivElement): Unit = {
      element.className = "div-styled"
      element.divSpecificProp = "special"
      println(s"Applied div-specific styling")
    }
  }

  object SpanModifier extends Modifier[SpanElement] {
    override def apply(element: SpanElement): Unit = {
      element.spanSpecificProp = "span-value"
      println(s"Applied span-specific styling")
    }
  }

// Demonstrating contravariance:
  val divElement = new DivElement
  val spanElement = new SpanElement

// Because of - contravariance, we can assign MORE GENERAL modifiers
// to variables expecting MORE SPECIFIC modifiers (reverse of covariance!)
  val divMod: Modifier[DivElement] = GeneralModifier // ✓ Works!
  val spanMod1: Modifier[SpanElement] = GeneralModifier // ✓ Works!
  val spanMod2: Modifier[SpanElement] = DivModifier // ✓ Works!

// Apply them
  divMod.apply(divElement) // Uses GeneralModifier on DivElement
  spanMod1.apply(spanElement) // Uses GeneralModifier on SpanElement
  spanMod2.apply(spanElement) // Uses DivModifier on SpanElement

// Practical use case - function that needs a specific modifier:
  def applyDivModifier(element: DivElement, mod: Modifier[DivElement]): Unit = {
    mod.apply(element)
  }

  applyDivModifier(divElement, GeneralModifier) // ✓ Works!
  applyDivModifier(divElement, DivModifier) // ✓ Works!
  // applyDivModifier(divElement, SpanModifier) // ✗ Doesn't work!
}
