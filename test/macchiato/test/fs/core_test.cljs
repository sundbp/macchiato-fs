(ns macchiato.test.fs.core-test
  (:require
    [macchiato.fs :as fs]
    [cljs.test :refer-macros [is are deftest testing use-fixtures]]))

(def text-path "test/data/foo.txt")
(def data-path "test/data/foo.edn")

(def text-value "this is a test")
(def data-value {:foo "bar"})

(deftest file-test
  (is (true? (fs/exists? "project.clj")))
  (is (true? (fs/file? "project.clj")))
  (is (false? (fs/directory? "project.clj")))
  (is (true? (fs/directory? "test"))))

(deftest spit-slurp-test
  (fs/spit text-path text-value)
  (fs/spit data-path data-value)
  (is (= text-value (fs/slurp text-path)))
  (is (= (str data-value) (fs/slurp data-path))))

(deftest spit-async-test
  (fs/spit text-path text-value)
  (fs/delete text-path)
  (fs/spit-async text-path text-value (fn [_] (is false))))

(deftest slurp-async-test
  (fs/delete text-path)
  (fs/spit text-path text-value)
  (fs/slurp-async text-path (fn [value] (is (= text-value value)))))

(deftest stat-test
  (is
    (= [:ino :uid :atime :mode :size :gid :ctime :nlink :blocks :blksize :birthtime :dev :rdev :mtime]
       (keys (fs/stat text-path)))))
