package gcd

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import chisel3.util.experimental.loadMemoryFromFileInline
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class Lab2Test extends AnyFreeSpec with Matchers with ChiselSim {
  "should perform simple program from lab 2" in {
    simulate(new ControlUnit("/lab2.txt")) { dut =>
      println(" IP |  CR |  AR |  DR |  SP |  BR |  AC | NZVC ||  AR |  DR ")
      dut.clock.stepUntil(dut.controlUnitOutput.HALT, 1, 1000)
    }
  }
}
