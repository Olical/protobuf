# protobuf

[![Clojars Project][clojars-badge]][clojars]

*A Clojure interface to Google's protocol buffers*

[![][logo]][logo-large]

**Contents**

* About
* Changes in this Fork
* Using Leiningen
* Getting Started
* Usage
* Documentation
* Release Notes
* Donating

See the Clojars page ([uk.me.oli/protobuf](https://clojars.org/uk.me.oli/protobuf)) for dependency information for this updated fork.

## About

This project provides a Clojure interface to Google's
[protocol buffers](http://code.google.com/p/protobuf). Protocol buffers can be
used to communicate with other languages over the network, and they are WAY
faster to serialize and deserialize than standard Clojure objects.

## Changes in this Fork

> Olical: This is just my attempt at merging together various forks to get all of the benefits in one pre-built package.

Changes in Protocol Buffer's "syntax3" require some changes in the library to correct 'bug's:

1. [mapdef.clj](src/clj/protobuf/impl/flatland/mapdef.clj) was failing at line 39 due to changes in the protobuf Java implementation. Specifically, messages are `Descriptors$FileDescriptor`, not the `Descriptors$Descriptor` required.
2. Add additional persistent map behaviours
3. Add support for default values
4. Updated protobuf to 3.21.12 which helps with default values for enums and other values being returned from decode functions.


## Using Leiningen

To run all the tests, type:

    lein test-all

To work at the repl, type:

    lein repl

There are some "Rich Comments" for repl'ing in:

* [syntax_3_test.clj](test/protobuf/examples/syntax_3_test.clj)
* [core.clj](src/clj/protobuf/core.clj)
* [mapdef.clj](src/clj/protobuf/impl/flatland/mapdef.clj)

## Getting Started

Add the dependency to your `project.clj`:

[![Clojars Project][clojars-badge]][clojars]

Then, given a project with the following in `resources/proto/your/namespace/person.proto`:

```proto
package your.namespace.person;

option java_outer_classname = "Example";

message Person {
  required int32  id    = 1;
  required string name  = 2;
  optional string email = 3;
  repeated string likes = 4;
}
```

you can compile the proto using the protobuf compiler and include the resulting
`.java` code in your project:

```shell
protoc \
  -I=/usr/include \
  -I=/usr/local/include \
  -I=resources/proto \
  --java_out=$OUT_DIR \
  resources/proto/your/namespace/*.proto
```

Note that, at this point, the files are `.java` source files, not `.class`
files; as such, you will still need to compile them.

We've found a clean way to do this (and how we set up the tests) is to:

* put these `.java` files in an isolated directory
* add that directory to a `:java-source-paths` entry in the `project.clj`
* place that in an appropriate `project.clj` profile


## Usage

Now you can use the protocol buffer in Clojure:

```clojure
(require '[protobuf.core :as protobuf])
(import '(your.namespace.person Example$Person))

(def alice (protobuf/create Example$Person
                            {:id 108
                             :name "Alice"
                             :email "alice@example.com"}))
```

Make some changes to the data and serialize to bytes:

```clj
(def b (-> alice
           (assoc :name "Alice B. Carol")
           (assoc :likes ["climbing" "running" "jumping"])
           (protobuf/->bytes)))
```

Round-trip the bytes back to a probuf object:

```clj
(protobuf/bytes-> alice b)
```

Which gives us:

```clj
{:id 108,
 :name "Alice B. Carol",
 :email "alice@example.com",
 :likes ["climbing" "running" "jumping"]}
```

The data stored in the instance is immutable just like other clojure objects.
It is similar to a struct-map, except that you cannot insert fields that aren't
specified in the `.proto` file.

(For instance, if you do a round trip with the data like we did above, but use
`:dislikes` -- not in the protobuf definition -- instead of `:likes`,
converting from bytes back to the protobuf instance will result in the
`:dislikes` key and associated value being dropped.)


## Documentation

The above usage is a quick taste; for more examples as well as the current and
previous reference documentation, visit the
[Clojure protobuf documentation][docs]. These docs include the following:

* Basic usage example
* A Clojure port of Google's Java protobuf tutorial
* An example for working with extensions
* Clojure protobuf API Reference
* Marginalia docs
* Documentation for the two Java classes (see the "javadoc" link there)


## Release Notes

| Version               | Notes
|-----------------------|------------------------------------------------------
| 1.0.0 (reset on fork) | Merged @mattyulrich, @cawasser and @fr33m0nk's changes
| 3.6.0-v1.2-SNAPSHOT   | Bumped to latest release of protobuf-java (see the branch [release/1.2.x](https://github.com/clojusc/protobuf/tree/release/1.2.x)), added byte and stream support in constructors, added benchmarking
| 3.5.1-v1.1            | Added docs, more func renames, new abstraction layer, improved DevEx of API, and fix for enums as Clojure keywords
| 3.5.1-v1.0            | Droped extra deps, renamed functions
| 3.5.1-v0.3            | Bumped to latest release of protobuf-java, re-added tests, Travis CI support
| 3.4.0-v0.2            | Transition release; identical to the ghaskins clojure-protobuf at version  3.4.0-2-SNAPSHOT

A note on the history: This project picked up the Clojure protobuf code base
from the [ghaskins fork](https://github.com/ghaskins/clojure-protobuf); its
last significant update was 2 years prior. The work at that point had been
given the version "3.4.0-2-SNAPSHOT", tracking the Protocol Buffer release of
3.4.0. We created a branch and tag for that release with no changes other than
the org/artifact id.


## Donating

> Olical: Leaving the original donation things here since all I've done is a bit of code gardening really, I'm not the author. I'm just curating other people's work and publishing it.

At the [request of a user][donation-request], a donation account for project
development has been set up on Liberapay here:

* [https://liberapay.com/clojusc-protobuf/donate](https://liberapay.com/clojusc-protobuf/donate)

You can learn more about Liberapay on its [Wikipedia entry][libera-wiki] or on the
service's ["About" page][libera-about].


<!-- Named page links below: /-->

[logo]: ux-resources/images/google-protocol-buffer-small.png
[logo-large]: ux-resources/images/google-protocol-buffer.png
[clojars]: https://clojars.org/uk.me.oli/protobuf
[clojars-badge]: https://img.shields.io/clojars/v/uk.me.oli/protobuf.svg
[docs]: https://clojusc.github.io/protobuf
[donation-request]: https://github.com/clojusc/protobuf/issues/29
[libera-wiki]: https://en.wikipedia.org/wiki/Liberapay
[libera-about]: https://liberapay.com/about/
