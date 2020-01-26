(ns optimus-sass.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [optimus.assets.creation :refer [last-modified existing-resource]]
            [optimus.assets.load-css :refer [create-css-asset]])
  (:import io.bit3.jsass.Options
           io.bit3.jsass.Compiler))

(defn compile-file [^java.net.URL file]
  (let [compiler (Compiler.)
        options (Options.)]
    (.compileFile compiler file (io/as-url "/tmp/test-sass.sass") options)))

(defn load-sass-asset [public-dir path]
  (let [resource (existing-resource public-dir path)]
    (-> (create-css-asset (str/replace path #"\.sass\z|\.scss\z" ".css")
                          (compile-file resource)
                          (last-modified resource))
        (assoc :original-path path))))

(doseq [ext ["sass" "scss"]]
  (defmethod optimus.assets.creation/load-asset ext
    [public-dir path]
    (load-sass-asset public-dir path)))
