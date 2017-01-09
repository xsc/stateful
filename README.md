# stateful

__[Documentation](https://xsc.github.io/stateful/)__

__stateful__ is an extension to Clojure's [test.check][test-check], providing a
way to communicate state between generators. This allows you to more easily
generate data with internal dependencies or track properties of generated
values.

[![Build Status](https://travis-ci.org/xsc/stateful.svg?branch=master)](https://travis-ci.org/xsc/stateful)

Note that, just like with `bind`, shrinking efficiency might suffer in cases
where you depend on the state influenced by other generators to generate new
values. This means that this library should mainly be used to track generation
metadata, like building the expected return value of some function together with
its input.

[test-check]: https://github.com/clojure/test.check

## Quickstart

__stateful__ allows you to have an implicit state value available in your
test.check generators by wrapping them using `stateful/generator`.

```clojure
(require '[stateful.core :as stateful]
         '[clojure.test.check.generators :as gen])
```

You can access the state using `stateful/state` or `stateful/value` and
manipulate it using e.g. `stateful/return` and its variants:

```clojure
(def ascending-integers
  (stateful/generator
    (gen/vector
      (gen/let [delta    gen/s-pos-int
                previous (stateful/value [:previous])]
        (let [value (+ delta previous)]
          (stateful/return value {:previous value}))))
    {:previous 0}))
```

The above example will use the state's `:previous` key to remember the previous
value, increasing it by `delta` for each new vector element:

```clojure
(gen/generate ascending-integers)
;; => [[30 60 82 96 120 140 167 191 220 236 267 291 314 336 349 369 379 392]
;;     {:previous 392}]
```

As you can see, a stateful generator produces a tuple of the generated value
and the final state.

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
