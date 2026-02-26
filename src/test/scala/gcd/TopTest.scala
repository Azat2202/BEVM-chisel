package gcd

import chisel3._
import chisel3.simulator.scalatest.ChiselSim
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class TopTest extends AnyFreeSpec with Matchers with ChiselSim{
  private val memory1Addr = 0x0.U
  private val memory2Addr = 0x7FF.U

  "Read two values from memory and substract them" in {
    simulate(new Top){ dut =>
      writeMemory(dut, memory1Addr, 555.U)
      writeMemory(dut, memory2Addr, 100.U)

      readMemoryToDR(dut, memory1Addr)
      expectReg(dut, 555.U, dut.flags.RDDR)


      moveDRToAC(dut)
      expectReg(dut, 555.U, dut.flags.RDAC)

      readMemoryToDR(dut, memory2Addr)

      sumACDR(dut)

      expectReg(dut, 455.U, dut.flags.RDAC)
    }
  }

  private def writeMemory(dut: Top, address: UInt, data: UInt): Unit = {
    dut.topInput.writeToRegisters.poke(true.B)
    dut.topInput.data.poke(data)
    dut.flags.WRDR.poke(true.B)

    dut.clock.step()

    dut.flags.WRDR.poke(false.B)
    dut.topInput.writeToRegisters.poke(true.B)
    dut.topInput.data.poke(address)
    dut.flags.WRAR.poke(true.B)

    dut.clock.step()

    dut.flags.WRAR.poke(false.B)
    dut.topInput.writeToRegisters.poke(false.B)
    dut.flags.STOR.poke(true.B)

    dut.clock.step()

    dut.flags.STOR.poke(false.B)
  }

  private def readMemoryToDR(dut: Top, address: UInt): Unit = {
    dut.topInput.writeToRegisters.poke(true.B)
    dut.topInput.data.poke(address)
    dut.flags.WRAR.poke(true.B)

    dut.clock.step()

    dut.flags.WRAR.poke(false.B)
    dut.flags.LOAD.poke(true.B)

    dut.clock.step()
    dut.flags.LOAD.poke(false.B)
  }

  private def moveDRToAC(dut: Top): Unit = {
    dut.flags.RDDR.poke(true.B)
    dut.flags.SORA.poke(false.B)
    dut.flags.LTOL.poke(true.B)
    dut.flags.HTOH.poke(true.B)
    dut.flags.WRAC.poke(true.B)

    dut.clock.step()

    dut.flags.RDDR.poke(false.B)
    dut.flags.SORA.poke(false.B)
    dut.flags.LTOL.poke(false.B)
    dut.flags.HTOH.poke(false.B)
    dut.flags.WRAC.poke(false.B)
  }

  private def sumACDR(dut: Top): Unit = {
    dut.flags.RDAC.poke(true.B)
    dut.flags.RDDR.poke(true.B)
    dut.flags.SORA.poke(false.B)
    dut.flags.PLS1.poke(true.B)
    dut.flags.COMR.poke(true.B)
    dut.flags.LTOL.poke(true.B)
    dut.flags.HTOH.poke(true.B)
    dut.flags.SETZ.poke(true.B)
    dut.flags.SETV.poke(true.B)
    dut.flags.SETC.poke(true.B)
    dut.flags.WRAC.poke(true.B)

    dut.clock.step()

    dut.flags.RDAC.poke(false.B)
    dut.flags.RDDR.poke(false.B)
    dut.flags.SORA.poke(false.B)
    dut.flags.PLS1.poke(false.B)
    dut.flags.COMR.poke(false.B)
    dut.flags.LTOL.poke(false.B)
    dut.flags.HTOH.poke(false.B)
    dut.flags.SETZ.poke(false.B)
    dut.flags.SETV.poke(false.B)
    dut.flags.SETC.poke(false.B)
    dut.flags.WRAC.poke(false.B)
  }

  private def expectReg(dut: Top, expected: UInt, readRegFlag: Bool): Unit = {
    readRegFlag.poke(true.B)
    dut.flags.SORA.poke(false.B)
    dut.flags.HTOH.poke(true.B)
    dut.flags.LTOL.poke(true.B)

    dut.clock.step()

    dut.topOutput.data.expect(expected)

    readRegFlag.poke(false.B)
  }
}
