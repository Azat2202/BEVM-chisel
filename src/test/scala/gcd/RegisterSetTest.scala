package gcd

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class RegisterSetTest extends AnyFreeSpec with Matchers with ChiselSim {

  "Register should be read and written" in
    simulate(new RegisterSet) { dut =>
      dut.input.data.poke(0x5555.S)
      dut.nzvcInput.N.poke(true.B)
      dut.nzvcInput.Z.poke(true.B)
      dut.nzvcInput.V.poke(true.B)
      dut.nzvcInput.C.poke(true.B)

      dut.flags.WRAC.poke(true.B)
      dut.flags.WRBR.poke(true.B)
//      dut.flags.WRPS.poke(true.B) (do not flush flags)
      dut.flags.WRDR.poke(true.B)
      dut.flags.WRCR.poke(true.B)
      dut.flags.WRIP.poke(true.B)
      dut.flags.WRSP.poke(true.B)

      dut.clock.step()

      dut.flags.WRAC.poke(false.B)
      dut.flags.WRBR.poke(false.B)
      dut.flags.WRDR.poke(false.B)
      dut.flags.WRCR.poke(false.B)
      dut.flags.WRIP.poke(false.B)
      dut.flags.WRSP.poke(false.B)

      dut.flags.RDAC.poke(true.B)
      dut.output.left.expect(0x5555.S)
      dut.flags.RDAC.poke(false.B)

      dut.flags.RDAC.poke(true.B)
      dut.output.left.expect(0x5555.S)
      dut.flags.RDAC.poke(false.B)

      dut.flags.RDBR.poke(true.B)
      dut.output.left.expect(0x5555.S)
      dut.flags.RDBR.poke(false.B)

      dut.flags.RDPS.poke(true.B)
      dut.output.left.expect(0x000f.S) // NZVC flags
      dut.flags.RDPS.poke(false.B)

      dut.flags.RDIR.poke(true.B)
      dut.output.left.expect(0x0000.S) // we cannot write to IR
      dut.flags.RDIR.poke(false.B)

      dut.flags.RDDR.poke(true.B)
      dut.output.right.expect(0x5555.S)
      dut.flags.RDDR.poke(false.B)

      dut.flags.RDCR.poke(true.B)
      dut.output.right.expect(0x5555.S)
      dut.flags.RDCR.poke(false.B)

      dut.flags.RDIP.poke(true.B)
      dut.output.right.expect(0x555.U(16.W).asSInt)
      dut.flags.RDIP.poke(false.B)

      dut.flags.RDSP.poke(true.B)
      dut.output.right.expect(0x555.U(16.W).asSInt)
      dut.flags.RDSP.poke(false.B)
    }
}
