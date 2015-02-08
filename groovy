import groovy.transform.*
import groovy.transform.builder.*

@Immutable
@ToString
@Builder(prefix='set', builderStrategy=InitializerStrategy)
class Value {
    int value
}

def builder = Value.createInitializer().setValue(1)
assert new Value(builder).toString()=='Value(1)'