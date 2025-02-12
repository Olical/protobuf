(ns protobuf.common)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-class
  [this]
  (:protobuf-class (.contents this)))

(defn get-instance
  [this]
  (:instance (.contents this)))

(defn get-wrapper
  [this]
  (:java-wrapper (.contents this)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Behaviours   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def common-behaviour
  {:get-class get-class
   :get-instance get-instance
   :get-wrapper get-wrapper})

(def associative-behaviour
  {:containsKey (fn [this data] (.containsKey (get-instance this) data))
   :entryAt (fn [this data] (.entryAt (get-instance this) data))})

(def iterable-behaviour
  {:forEach (fn [this consumer] (.forEach (get-instance this) consumer))
   :iterator (fn [this] (.iterator (get-instance this)))
   :spliterator (fn [this] (.spliterator (get-instance this)))})

(def lookup-behaviour
  {:valAt (fn ([this k] (.valAt (get-instance this) k))
              ([this k fallback] (.valAt (get-instance this) k fallback)))})

(def persistent-collection-behaviour
  {:cons (fn [this o] (.cons (get-instance this) o))
   :count (fn [this] (.count (get-instance this)))
   :empty (fn [this] (.empty (get-instance this)))
   :equiv (fn [this o]
           (and (= (get-class this) (get-class o))
                (.equiv (get-instance this) (get-instance o))))})

(def persistent-map-behaviour
  {:assoc (fn [this k v] (.assoc (get-instance this) k v))
   :assocEx (fn [m k v] (throw (new Exception)))
   :without (fn [this k] (.without (get-instance this) k))})

(def printable-behaviour
  {:toString (fn [this] (.toString (get-instance this)))})

(def seqable-behaviour
  {:seq (fn [this] (.seq (get-instance this)))})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Additional Map Behaviours   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def additional-persistent-map-behaviours
  {:assoc-in (fn [protobuf-object ks v]
               (assoc-in protobuf-object ks v))
   :update-in (fn [protobuf-object ks fn]
                (update-in protobuf-object ks fn))
   :merge (fn [& protobuf-objects]
            (merge (first protobuf-objects)))
   :merge-with (fn [fn & protobuf-objects]
                 (merge-with fn (first protobuf-objects)))})