def codenarcReport = new File('./build/reports/codenarc/main.xml')
assert codenarcReport.isFile()

def pattern = ~/(?s)<CodeNarc.+>/
def xml = (codenarcReport.text =~ pattern)[0]

def parser = new XmlSlurper()
def rootNode = parser.parseText(xml)


def codeNarcP1 = rootNode.PackageSummary.@priority1.text()
def codeNarcP2 = rootNode.PackageSummary.@priority2.text()
def codeNarcP3 = rootNode.PackageSummary.@priority3.text()

println "Priority 1=$codeNarcP1"
println "Priority 2=$codeNarcP2"
println "Priority 3=$codeNarcP3"
