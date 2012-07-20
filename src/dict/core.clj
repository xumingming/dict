(ns dict.core
  (:require [clojure.xml :as xml])
  (:gen-class))

(defn fetch [word]
  (let [url (str "http://dict.youdao.com/fsearch?q=" word)
        body (xml/parse url)
        content (-> body :content)
        custom-translation (first (filter #(= :custom-translation (:tag %)) content))
        custom-translation (:content custom-translation)
        translations (filter #(= :translation (:tag %)) custom-translation)
        translations (map #(-> % :content first :content first) translations)]
    {:translations translations}))

(defn look [word]
  (let [translations (fetch word)
        translations (:translations translations)]
    (doseq [translation translations]
      (println translation))))

(defn -main [word]
  (look word))
