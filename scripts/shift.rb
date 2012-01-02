require 'java'
java_import 'org.tal.rubychip.RubyCircuit'

class Shift < RubyCircuit
  
  def init
    info "shift " + inputs.length.to_s() + " input(s). "
    if outputs.length!=1 || inputs.length<2
      error "expecting 1 output and 2 or more inputs."
      return false
    end 
    
    @count = 0
    
    true
  end

  def input(idx, state)
    if idx==0 && state
       send 0, inputs[@count+1]
       count
    end
  end
  
  def count()    
    if @count < inputs.length-2
      @count = @count + 1
    else
      @count = 0
    end
    
    if has_debuggers
      debug "count=" + @count.to_s()
    end
  end
end

Shift.new()
