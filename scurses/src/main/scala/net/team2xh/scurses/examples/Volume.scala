package net.team2xh.scurses.examples

import net.team2xh.scurses.examples.VolumeEngine.LinearAlgebra.Vector3

import scala.collection.mutable
import scala.collection.mutable._

import net.team2xh.scurses._


object VolumeEngine {
  object LinearAlgebra {
    case class Matrix3(a: Vector3, b: Vector3, c: Vector3) {
      def *(other: Matrix3): Matrix3 = {
        Matrix3(Vector3(this.a.x * other.a.x + this.b.x * other.a.y + this.c.x * other.a.z,
                        this.a.y * other.a.x + this.b.y * other.a.y + this.c.y * other.a.z,
                        this.a.z * other.a.x + this.b.z * other.a.y + this.c.z * other.a.z),
                Vector3(this.a.x * other.b.x + this.b.x * other.b.y + this.c.x * other.b.z,
                        this.a.y * other.b.x + this.b.y * other.b.y + this.c.y * other.b.z,
                        this.a.z * other.b.x + this.b.z * other.b.y + this.c.z * other.b.z),
                Vector3(this.a.x * other.c.x + this.b.x * other.c.y + this.c.x * other.c.z,
                        this.a.y * other.c.x + this.b.y * other.c.y + this.c.y * other.c.z,
                        this.a.z * other.c.x + this.b.z * other.c.y + this.c.z * other.c.z))
      }
    }
    object Matrix3 {
      val zero = Matrix3(Vector3.zero, Vector3.zero, Vector3.zero)
      val identity = Matrix3(Vector3(1, 0, 0), Vector3(0, 1, 0), Vector3(0, 0, 1))
    }

    case class Vector3(x: Double, y: Double, z: Double)
    object Vector3 {
      val zero = Vector3(0, 0, 0)
    }

    case class Vector2(x: Double, y: Double)
  }
  import LinearAlgebra._

  type Color = (Int, Int, Int)
  case class Camera(position: Matrix3 = Matrix3.zero, target: Matrix3 = Matrix3.zero)
  case class Mesh(name: String, vertices: mutable.Seq[Vector3] = ArrayBuffer[Vector3](),
                  position: Matrix3 = Matrix3.zero, rotation: Matrix3 = Matrix3.zero)
  case class Screen(screen: Scurses) {
    val (width, height) = screen.size

    def clear() = screen.clear()
    def refresh() = screen.refresh()
    def putPixel(x: Int, y: Int, color: Color): Unit = {
      screen.put(x, y, "█", foreground = Colors.fromRGB(color))
    }
    def project(coord: Vector3, transformationMatrix: Matrix3): Vector2 = {
      ???
    }

  }
}

object Volume extends App {
  Scurses { screen =>

    val cube = VolumeEngine.Mesh("cube", ArrayBuffer(
      Vector3(-1, -1, -1),
      Vector3(-1, -1,  1),
      Vector3(-1,  1, -1),
      Vector3( 1, -1, -1),
      Vector3(-1,  1,  1),
      Vector3( 1, -1,  1),
      Vector3( 1,  1, -1),
      Vector3( 1,  1,  1)
    ))

    screen.refresh()
    screen.keypress()
  }
}
