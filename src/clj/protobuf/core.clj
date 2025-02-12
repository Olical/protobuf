(ns protobuf.core
  "This is the public API for working with protocol buffers."
  (:require
    [protobuf.common :as common]
    [protobuf.impl.flatland.core :as flatland])
  (:import
    (protobuf.impl.flatland.core FlatlandProtoBuf)
    (protobuf PersistentProtocolBufferMap))
  (:refer-clojure :exclude [map? read assoc-in update-in merge merge-with]))

(defprotocol AdditionalPersistentMapAPI
  (assoc-in [protobuf-object ks v])
  (update-in [protobuf-object ks fn])
  (merge [& protobuf-objects])
  (merge-with [fn & protobuf-objects]))


(defprotocol ProtoBufCommonAPI
  (get-class [this])
  (get-instance [this])
  (get-wrapper [this]))

(defprotocol ProtoBufAPI
  (->bytes [this])
  (->schema [this])
  (bytes-> [this bytes])
  (syntax [this])
  (read [this in])
  (write [this out]))

(extend FlatlandProtoBuf
  ProtoBufCommonAPI
  common/common-behaviour)

(extend FlatlandProtoBuf
  ProtoBufAPI
  flatland/behaviour)

(extend FlatlandProtoBuf
  AdditionalPersistentMapAPI
  common/additional-persistent-map-behaviours)

(extend PersistentProtocolBufferMap
  AdditionalPersistentMapAPI
  common/additional-persistent-map-behaviours)

(def default-impl-name "flatland")

(defn get-impl
  "Get the currently configured protobuf implementation. If not defined,
  used the hard-coded default value (see `default-impl-name`).

  Note that the protobuf backend implementation is configured using
  JVM system properties (i.e., the `-D` option). Projects that use `lein`
  may set this with `:jvm-opts`
  (e.g, `:jvm-opts [\"-Dprotobuf.impl=flatland\"]`)."
  []
  (keyword (or (System/getProperty "protobuf.impl")
             default-impl-name)))

(defn schema
  "This function is designed to be called against compiled Java protocol
  buffer classes. To get the schema of a Clojure protobuf instance, you'll
  want to use the `->schema` method."
  [protobuf-class]
  (case (get-impl)
    :flatland (FlatlandProtoBuf/schema protobuf-class)))

(defn create
  ([protobuf-class]
   (create protobuf-class {}))
  ([protobuf-class data]
   (create (get-impl) protobuf-class data))
  ([impl-key protobuf-class data]
   (case impl-key
     :flatland (new FlatlandProtoBuf protobuf-class data))))




(comment
  ; looking into problems with nested types in syntax3 (not sure if syntax 2 has he same issue, or
  ; is it something about cross-file imports?

  (import 'protobuf.examples.message3.Person3)

  Person3
  (def protobuf-class Person3)
  (def data {:id 7 :name "Test Person"})
  (def impl-key :flatland))
