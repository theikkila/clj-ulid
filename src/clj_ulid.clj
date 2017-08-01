(ns clj-ulid
  (:refer-clojure :exclude [rand rand-int rand-nth bytes])
  (:import [java.security SecureRandom]))

(def ^:const ^String BASE32_DEFAULT
  "Base32 Alphabet."
  "0123456789abcdefghjkmnpqrstvwxyz")

(def ^:const ^Integer BASE32_VALUES
  "Base32 values"
  (range 32))

(def ^:const base32-value-map (zipmap BASE32_DEFAULT BASE32_VALUES))
(def ^:const value-base32-map (zipmap BASE32_VALUES BASE32_DEFAULT))

(def ^:const encoding-length (count BASE32_DEFAULT))



(defmacro wrap-ignore-exception [& body]
  `(try
     ~@body
     (catch Throwable e#)))

(defn- ^SecureRandom new-random
  "Try to create a appropriate SecureRandom.
   http://www.cigital.com/justice-league-blog/2009/08/14/proper-use-of-javas-securerandom/ "
  []
  (doto
      (or
       (wrap-ignore-exception (SecureRandom/getInstance "SHA1PRNG" "SUN"))
       (wrap-ignore-exception (SecureRandom/getInstance "SHA1PRNG"))
       (wrap-ignore-exception (SecureRandom.)))
    (.nextBytes (byte-array 16))))

(defonce ^ThreadLocal threadlocal-random (proxy [ThreadLocal] []
                                           (initialValue [] (new-random))))



(defn- rand
 "Returns a secure random floating point number between 0 (inclusive) and
 n (default 1) (exclusive)."
 {:static true}
 ([] (.nextDouble ^SecureRandom (.get threadlocal-random)))
 ([n] (* n (rand))))

(defn- rand-int
 "Returns a secure random integer between 0 (inclusive) and n (exclusive)."
 {:static true}
 [n] (int (rand n)))

(defn- rand-nth
 "Return a secure random element of the (sequential) collection. Will have
 the same performance characteristics as nth for the given
 collection."
 {:static true}
 [coll]
 (nth coll (rand-int (count coll))))



(defn encode
  "Encode integer with Crockford's Base32"
  {:static true}
  [n length]
  (->
    (reduce
      (fn [state _]
        (let [{:keys [n encoded]} state
              m (rem n encoding-length)
              n-next (/ (- n m) encoding-length)]
          {:encoded (str (get value-base32-map m) encoded)
           :n n-next}))
      {:encoded "" :n (max n 0)}
      (range length))
    :encoded))


(defn decode [s]
  "Decode string to integer with Crockford's Base32"
  {:static true}
  (as-> s $
    (reverse $)
    (map (partial get base32-value-map) $)
    (map bit-shift-left $ (iterate (partial + 5) 0))
    (reduce bit-or $)))

(defn- now []
  (System/currentTimeMillis))

(defn random-base32-string
  ([] (random-base32-string 16))
  ([length]
   (->> (repeatedly #(rand-nth BASE32_DEFAULT))
        (take length)
        (apply str))))



(defn ulid
  "Generate ULID from time seed, fallback to current time if no seed is passed"
  ([] (ulid (now)))
  ([time]
   (str (encode time 10) (random-base32-string))))

(defn ulid->timestamp [ulid-string]
  "Get timestamp from ULID"
  (-> ulid-string
    (subs 0 10)
    (clojure.string/lower-case)
    (decode)))
