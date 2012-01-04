require 'java'
import 'org.tal.rubychip.RubyCircuit'

class CLASS123597132467298 < RubyCircuit
  def init
    info "Running default script. To program the chip enter /rubyc"
    true
  end

  def input(idx, state)
    debug idx.to_s() + ": " + state.to_s()
    send idx, state
  end
end

CLASS123597132467298.new()