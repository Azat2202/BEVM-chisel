package gcd

import chisel3._
import chisel3.experimental.BundleLiterals._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AluTest extends AnyFreeSpec with Matchers with ChiselSim {

  "ALU should perform simple addition and test C0" in
    simulate(new Alu) { dut =>
      dut.input.left.poke(10.S)
      dut.input.right.poke(5.S)
      dut.input.C.poke(true.B)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(false.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(15.S)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
      dut.output.C0.expect(true.B)
    }

  "ALU should perform addition with PLS1" in
    simulate(new Alu) { dut =>
      dut.input.left.poke(10.S)
      dut.input.right.poke(5.S)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(true.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(16.S)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }

  "ALU should perform AND operation" in
    simulate(new Alu) { dut =>
      dut.input.left.poke(0b1010101010101010.S)
      dut.input.right.poke(0b1111000011110000.S)
      dut.flags.SORA.poke(true.B)
      dut.flags.PLS1.poke(false.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(0b1010000010100000.U.asSInt)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }

  "ALU should perform AND + 1 when PLS1 is true" in
    simulate(new Alu) { dut =>
      dut.input.left.poke(0xf0f0.S)
      dut.input.right.poke(0x0ff0.S)
      dut.flags.SORA.poke(true.B)
      dut.flags.PLS1.poke(true.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(((0xf0f0 & 0x0ff0) + 1).U(16.W).asSInt)
      dut.output.C.expect(false.B)
      dut.output.C14.expect(false.B)
    }

  "ALU should perform A - B" in
    simulate(new Alu) { dut =>
      dut.input.left.poke(15.S)
      dut.input.right.poke(4.S)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(true.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(true.B)

      dut.output.data.expect(11.S)
      dut.output.C.expect(true.B)
      dut.output.C14.expect(true.B)
    }

  "ALU should handle overflow correctly" in
    simulate(new Alu) { dut =>
      dut.input.left.poke(0xffff.U(16.W).asSInt)
      dut.input.right.poke(1.S)
      dut.flags.SORA.poke(false.B)
      dut.flags.PLS1.poke(false.B)
      dut.flags.COML.poke(false.B)
      dut.flags.COMR.poke(false.B)

      dut.output.data.expect(0.S)
      dut.output.C.expect(true.B)
      dut.output.C14.expect(true.B)
    }
}
