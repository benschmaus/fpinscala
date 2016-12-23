package fpinscala.datastructures

sealed trait List[+A] // `List` data type, parameterized on a type, `A`

case object Nil extends List[Nothing] // A `List` data constructor representing the empty list
/* Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
which may be `Nil` or another `Cons`.
 */

case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List { // `List` companion object. Contains functions for creating and working with lists.

  def sum(ints: List[Int]): Int = ints match { // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)
  }

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  val x = List(1,2,3,4,5) match {
    case Cons(x, Cons(2, Cons(3, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(z, Cons(4, _)))) => y + z
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match {
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))
    }

  def foldRight[A,B](as: List[A], z: B)(f: (A, B) => B): B = // Utility functions
    // as match {
    //   case Nil => z
    //   case Cons(x, xs) => f(x, foldRight(xs, z)(f))
    // }
    foldLeft(as, (b:B) => b)((g,a) => b => g(f(a,b)))(z)

  def sum2(ns: List[Int]) =
    foldRight(ns, 0)((x,y) => x + y)

  def product2(ns: List[Double]) = {
    
    def mult(x: Double, y: Double) = {
      // println(x + " * " + y)
      x * y
      // if ((x >= 1.0) && (y >= 1.0)) x * y else 0.0
    }

    foldRight(ns, 1.0)((x, y) => mult(x, y)) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar
  }


  def tail[A](l: List[A]): List[A] = l match {
    case Cons(h, t) => t
    case Nil => List()
  }


  def setHead[A](l: List[A], h: A): List[A] = l match {
    case Cons(ch, t) => Cons(h, Cons(ch, t))
    case Nil => Cons(h, Nil)
  }

  def remove[A](l: List[A], n: Int): List[A] = l match {
    case Cons(h, t) => 
      if (h == n) drop(t, n)
      else Cons(h, drop(t, n)) 
    case Nil => List()
  }

  def drop[A](l: List[A], n: Int): List[A] = {
    def go(l: List[A], acc: Int): List[A] =  
      if (acc < n) go(tail(l), acc + 1)
      else l
          
    go(l, 0)
  }

  def dropWhile[A](l: List[A])(f: A => Boolean): List[A] = l match {
    case Cons(h, t) =>
      if (f(h)) dropWhile(t)(f)
      else Cons(h, dropWhile(t)(f))
    case Nil => l
  }

  def init[A](l: List[A]): List[A] = {
    def go(l: List[A], a: List[A]): List[A] = l match {
        case Cons(h, t) => 
          if (length(t) == 0) a
          else go(t, append(a, Cons(h, Nil)))
        case Nil => List()
    }          
    go(l, List())
  }

  def length[A](l: List[A]): Int = {
    def go(l: List[A], acc: Int): Int = l match {
      case Cons(h, t) => go(tail(l), acc + 1)
      case Nil => acc
    }
          
    go(l, 0)
  }

  def length2[A](l: List[A]): Int = {
    foldRight(l, 0)(
      (i, acc) => {
        // println("i = " + i + ", acc = " + acc)
        acc + 1
      }
    )
  }

  def foldLeft[A,B](l: List[A], z: B)(f: (B, A) => B): B = l match {
    case Cons(h, t) => foldLeft(t, f(z, h))(f)
    case Nil => z
  }

  def sum3(ns: List[Int]) =
    foldLeft(ns, 0)((x,y) => x + y)

  def length3[A](l: List[A]): Int = 
    foldLeft(l, 0)(
      (x, y) => x + 1
    )

  def product3(l: List[Double]) = 
    foldLeft(l, 1.0)(_ * _)

  def reverse[A](l: List[A]): List[A] =
    // foldLeft(l, Nil: List[A])(
    //   (acc, v) => setHead(acc, v)
    // )
    foldRight(l, Nil: List[A])(
      (v, acc) => append(acc, Cons(v, Nil))
    )

  // def reverse[A](l: List[A]): List[A] = foldLeft(l, List[A]())((acc,h) => Cons(h,acc))

  def foldRight2[A,B](as: List[A], z: B)(f: (A, B) => B): B = 
    // foldLeft(as, z)(
    //   (v, acc) => f(foldLeft(tail(as), acc)(f), z)
    // )
    ???

  def appendViaFold[A](l1: List[A], l2: List[A]): List[A] = 
    foldRight(l1, l2)(
      (v, acc) => Cons(v, acc)
    )

  def appendViaFold2[A](l1: List[A], l2: List[A]): List[A] = 
    foldLeft(reverse(l1), l2)(
      (acc, v) => Cons(v, acc)
    )

  def flatten[A](l: List[List[A]]): List[A] = {
    
    def go(from: List[A], to: List[A]): List[A] = from match {
        case Cons(h, t) => {
          // println(h)
          setHead(go(t, to), h)
        }
        case Nil => to
      }

    foldRight(l, List[A]())(
      (a, acc) => go(a, acc)
    )
  }

  def flattenLeft[A](l: List[List[A]]): List[A] = {
    
    def go(from: List[A], to: List[A]): List[A] = from match {
        case Cons(h, t) => {
          // println(h)
          Cons(h, go(t, to))
        }
        case Nil => to
      }

    foldLeft(l, List[A]())(
      (acc, a) => go(a, acc)
    )
  }

  def map[A,B](l: List[A])(f: A => B): List[B] = 
    foldRight(l, List[B]())(
      (a, acc) => append(Cons(f(a), Nil), acc)
    )


  def filter[A](as: List[A])(f: A => Boolean): List[A] = as match {
    case Cons(h, t) => 
      if (f(h)) Cons(h, filter(t)(f))
      else filter(t)(f)
    case Nil => Nil
  }

  def filter2[A](l: List[A])(f: A => Boolean): List[A] = 
    foldRight(l, List[A]())((h,t) => if (f(h)) Cons(h,t) else t)

  def filter3[A](l: List[A])(f: A => Boolean): List[A] = 
    flatMap(
      l)(
      (a) => {
        if (f(a)) List(a)
        else Nil        
      })

  def flatMap[A,B](as: List[A])(f: A => List[B]): List[B] =
    flatten(map(as)(f))


  def addLists(a: List[Int], b: List[Int]): List[Int] = {
    a match {
      case Cons(h, t) => b match {
        case Cons(h1, t1) => Cons(h + h1, addLists(tail(a), tail(b)))
        case Nil => Nil
      }
      case Nil => Nil
    }
    
  }

  def zipWith[A,B,C](a: List[A], b: List[B])(f: (A, B) => C): List[C] = {
    a match {
      case Cons(h, t) => b match {
        case Cons(h1, t1) => Cons(f(h, h1), zipWith(tail(a), tail(b))(f))
        case Nil => Nil
      }
      case Nil => Nil
    }
    
  }

  def hasPrefix[A](sup: List[A], pre: List[A]): Boolean = (sup, pre) match {
    case (_, Nil) => true
    case (Nil, _) => false
    case (Cons(h1, t1), Cons(h2, t2)) => 
      if (h1 == h2) hasPrefix(t1, t2)
      else false
  }


  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = sup match {
    case Nil => false
    case Cons(h, t) => 
      hasPrefix(sup, sub) || hasSubsequence(t, sub)
  }

}

final object DataMain extends App {

  import List._

  val x = List[Int](1,2,3,4,5,6,7,8,9,10)
  // println(tail(x))
  // println(setHead(x, 0))
  // println(setHead(List(), 1))
  // println(drop(x, 3))
  // println(
  //   dropWhile(x)(a => a >= 4)
  // )
  // println(length(x))
  // println(init(x))
  // println(product2(List(1.0, 2.0, 3.0, 4.0)))
  // println(foldRight(List(1,2,3), Cons(4, Nil))(
  //   (x, y) => {
  //     println("x = " + x + ", y = " + y)
  //     Cons(x, y)
  //   }
  // ))
  // println(length2(x))
  // println(sum3(x))
  // println(product3(List(1.0, 2.0, 3.0, 4.0)))
  // println(length3(x))
  // println(reverse(x))

  // val y = List(List(1,2,3), List(4,5,6))
  // println(y)
  // println(flatten(y))
  // println(flattenLeft(y))

  // println(map(x)(_ + 1))

  // def even = (x: Int) => (x % 2) == 0
  // println(
  //   filter3(x)(even)
  // )
  // println(
  //   filter2(x)(even)
  // )
  // println(
  //   filter(x)(even)
  // )

  // println(flatMap(List(1,2,3))(i => List(i,i)))

  // println(addLists(List(1,2,3), List(4,5,6)))
  // println(zipWith(List(1,2,3), List(4,5,6))(_ + _))

  println(hasSubsequence(List(1,2,3,4), List(1,4))) // f
  println(hasSubsequence(List(1,2,3,4), List(3,4))) // t
  println(hasSubsequence(List(1,2,3,4), List(1,2))) // t
  println(hasSubsequence(List(1,2,3,4), List(2,3,4))) // t
  println(hasSubsequence(List(1,2,3,4), List(2,1))) // f

}
