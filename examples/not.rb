require 'java'
java_import 'org.tal.rubychip.RubyCircuit'

class Not < RubyCircuit
   def init
      info "rubyc not"
      true
   end

   def input(idx, state)
      debug idx.to_s() + ": " + state.to_s()
      out idx, !state
   end
end

Not.new()
