package garantito.tsa

import ratpack.launch.LaunchConfigFactory
import ratpack.groovy.launch.GroovyRatpackMain

class TSAMain extends GroovyRatpackMain {
  public static void main(String[] args) {
    println "Iniciando servidor TSA"
    new TSAMain().start()    
  }

  @Override
  protected void addImpliedDefaults(Properties props) {
    super.addImpliedDefaults(props)
    println "Configurando servidor TSA"
    // por algún motivo Ratpack quiere que las properties estén como strings,
    // incluso cuando son números...
    props.put(LaunchConfigFactory.Property.PORT, '5050')
  }
}

