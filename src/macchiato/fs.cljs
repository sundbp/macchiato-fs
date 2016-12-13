(ns macchiato.fs
  (:refer-clojure :exclude [exists?]))

(def fs (js/require "fs"))

(def path-separator (.-sep (js/require "path")))

(defn- obj->clj [obj]
  (reduce
    (fn [props k]
      (assoc props (keyword k) (aget obj k)))
    {}
    (js/Object.keys obj)))

(defn exists? [path]
  (.existsSync fs path))

(defn file? [path]
  (.isFile (.lstatSync fs path)))

(defn directory? [path]
  (.isDirectory (.lstatSync fs path)))

(defn slurp [filename & {:keys [encoding]}]
  (when (exists? filename)
    (.toString
      (if encoding
        (.readFileSync fs filename encoding)
        (.readFileSync fs filename)))))

(defn slurp-async [filename cb & {:keys [encoding]}]
  (when (exists? filename)
    (let [str-cb (fn [err data] (cb err (.toString data)))]
      (if encoding
        (.readFile fs filename encoding str-cb)
        (.readFile fs filename str-cb)))))

(defn spit [filename data & {:keys [encoding mode flag]
                             :or   {encoding "utf8"
                                    mode     "0o666"
                                    flag     "w"}}]
  (let [data (if (string? data) data (str data))]
    (.writeFileSync fs filename data encoding mode flag)))

(defn spit-async [filename data on-error & {:keys [encoding mode flag]
                                            :or   {encoding "utf8"
                                                   mode     "0o666"
                                                   flag     "w"}
                                            :as   opts}]
  (.writeFile fs filename data (clj->js opts) on-error))

(defn delete [file]
  (.unlinkSync fs file))

(defn delete-async [file on-error]
  (.unlink fs file on-error))

(defn stat [path]
  (when (exists? path)
    (obj->clj (.statSync fs path))))

(defn stat-async [path cb]
  (when (exists? path)
    (.stat fs path (fn [err stats] (cb err (obj->clj stats))))))

(defn stream [path]
  (.createReadStream fs path))
