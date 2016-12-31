# stateful-generators

__stateful-generators__ is an extension to Clojure's [test.check][test-check],
providing a way to communicate state between generators. This allows you to
generate data with internal dependencies.

[test-check]: https://github.com/clojure/test.check

## Usage

Coming Soon.

```clojure
(require '[stateful-generators.core :as gen])

(def ascending-integers
   (gen/with-scope {:previous 0}
     (gen/vector
       (gen/with [{:keys [previous]}]
         (gen/let [value (gen/fmap #(+ previous %) gen/s-pos-int)]
           (gen/return {:previous value} value))))))

(gen/sample (gen/bound ascending-integers))
;; => ([] [3] [3 5] [4 8 12] [4 7] [3 5 10 14] [6 8 13 14 21 24]
;;     [5] [] [7 17 26 36 39 46 48])
```

## License

```
MIT License

Copyright (c) 2016 Yannick Scherer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
