require 'java'
java_import 'org.tal.rubychip.RubyCircuit'

COLOR1 = 15
COLOR2 = 14

class Disp1 < RubyCircuit
  def init
    info "disp demo 1 (args: width height [color1 color2])"

    if args.length<2
      error("args: width height [color1 color2]")
      return false
    end
    
    @dispWidth = args[0].to_i
    @dispHeight = args[1].to_i
    @direction = 1    

    if args.length>2
      @color1 = args[2].to_i
    else @color1 = COLOR1
    end
    
    if args.length>3
      @color2 = args[3].to_i
    else @color2 = COLOR2
    end
    
    cleardisplay

    @x = 0
    @y = 0
    @lastX = -1
    @lastY = -1
    
    true
  end

  def input(idx, state)
    # when getting clock
    if idx==0 
      if state
        step
      end      
    elsif idx<=4 
      @color1 = bits_to_i(1, 4, inputs)
      cleardisplay
    elsif idx<=8
      @color2 = bits_to_i(5, 4, inputs)        
    end    
  end
  
  # advances the animation by one frame.
  def step
    stepy
    debug("x=#{@x} y=#{@y}")
    if @lastX>=0 
      paint @lastX, @lastY, @color1
    end
    
    paint @x, @y, @color2
    
    @lastX = @x
    @lastY = @y
  end
  
  # set color of a pixel.
  def paint(x, y, color)
    send 1, 3, x
    send 4, 3, y
    send 7, 4, color
    send 0, true
    send 0, false
  end
  
  def stepy
    @y += @direction
    if @y>=@dispHeight
      @y -= 1
      @direction = -@direction
      stepx
    else
      if @y<0
        @y = 0
        @direction = -@direction
        stepx
      end
    end
    
  end
  
  def stepx
    @x += 1
    if @x>=@dispWidth
      @x = 0
    end
  end
  
  # sets all display pixels to the background color.
  def cleardisplay
    for x in 0..@dispWidth-1
      for y in 0..@dispHeight-1
        paint x, y, @color1
      end
    end
  end
end

Disp1.new()
