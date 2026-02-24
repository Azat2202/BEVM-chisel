package gcd

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AluTest extends AnyFreeSpec with Matchers with ChiselSim {

  "ALU should perform simple addition and test C0" in {
    simulate(new Alu) { dut =>
      dut.input.left.poke(10.U)
      dut.input.right.poke(5.U)
      dut.input.C.poke(true.B)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(false.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(15.U)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
      dut.output.C0.expect(true.B)
    }
  }

  "ALU should perform addition with PLS1" in {
    simulate(new Alu) { dut =>
      dut.input.left.poke(10.U)
      dut.input.right.poke(5.U)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(true.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(16.U)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }
  }

  "ALU should perform AND operation" in {
    simulate(new Alu) { dut =>
      dut.input.left.poke("b1010101010101010".U)
      dut.input.right.poke("b1111000011110000".U)
      dut.flags.SORA.poke(true.B)
      dut.flags.PLS1.poke(false.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect("b1010000010100000".U)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }
  }

  "ALU should perform AND + 1 when PLS1 is true" in {
    simulate(new Alu) { dut =>
      dut.input.left.poke(0xf0f0.U)
      dut.input.right.poke(0x0ff0.U)
      dut.flags.SORA.poke(true.B)
      dut.flags.PLS1.poke(true.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect((0xf0f0 & 0x0ff0 + 1).U)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }
  }

  "ALU should perform A - B" in {
    simulate(new Alu) { dut =>
      dut.input.left.poke(15.U)
      dut.input.right.poke(4.U)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(true.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(true.B)

      dut.output.data.expect(11.U)
      dut.output.C.expect(true.B)
      dut.output.C14.expect(true.B)
    }
  }

  "ALU should handle overflow correctly" in {
    simulate(new Alu) { dut =>
      dut.input.left.poke(65535.U) // 0xFFFF
      dut.input.right.poke(1.U)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(false.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(0.U)
      dut.output.C.expect(true.B)
      dut.output.C14.expect(true.B)
    }
  }
}
