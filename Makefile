.PHONY: nrepl node_modules shadow

mcp-shadow-dual:
	clojure -X:mcp-shadow-dual

nrepl:
	clojure -M:nrepl

node_modules:
	npm install

shadow: node_modules
# must open the web page to have a running repl
	npx shadow-cljs watch app portfolio
