require 'java'
java_import 'org.tal.rubychip.RubyCircuit'

class DisplayCircuit < RubyCircuit
  def initdisplay
    if args.length<2
      error("Expecting at least 2 args: display width and height.")
      return false
    end
    
    @dispWidth = args[0].to_i
    @dispHeight = args[1].to_i

    @xbits = calc_bit_length(@dispWidth)
    @ybits = calc_bit_length(@dispHeight)
    @colorbits = outputs.length - @xbits - @ybits - 1
    
    info("Display controller. " + @xbits.to_s + " x bits. " + @ybits.to_s + " y bits. " + @colorbits.to_s + " color bits.")
    true
  end

  def paint(x, y, color)
    send 1, @xbits, x
    send 1+@xbits, @ybits, y
    send 1+@xbits+@ybits, @colorbits, color
    send 0, true
    send 0, false
  end
  
  def cleardisplay(color)
    for x in 0..@dispWidth-1
      for y in 0..@dispHeight-1
        paint x, y, color
      end
    end    
  end
  
  def calc_bit_length(maxnum)
    return (Math.log(maxnum)/Math.log(2)).ceil;
  end
end
