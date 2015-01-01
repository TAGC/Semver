def coverageFile = new File('./build/reports/jacoco/test/jacocoTestReport.xml')
assert coverageFile.isFile()

def pattern = ~/<report.+>/
def xml = (coverageFile.text =~ pattern)[0]

def parser = new XmlSlurper()
def rootNode = parser.parseText(xml)

def instructionCounter = rootNode.counter.find { it.@type.text() == 'INSTRUCTION' }

def instructionsMissed = instructionCounter.@missed.text().toInteger()
def instructionsCovered = instructionCounter.@covered.text().toInteger()

def totalInstructions = instructionsMissed + instructionsCovered
def coverage = instructionsCovered / totalInstructions

println instructionsMissed
println instructionsCovered
println sprintf('Coverage=%.1f%%', coverage*100)
