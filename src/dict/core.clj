(ns dict.core
  (:require [clojure.xml :as xml])
  (:gen-class))

(defn fetch [word]
  (let [url (str "http://dict.youdao.com/fsearch?q=" word)
        yodaodict (xml/parse url)
        yodaodict (-> yodaodict :content)
        custom-translation (first (filter #(= :custom-translation (:tag %)) yodaodict))
        custom-translation (:content custom-translation)
        translations (filter #(= :translation (:tag %)) custom-translation)
        translations (map #(-> % :content first :content first) translations)
        phonetic-symbol (filter #(= :phonetic-symbol (:tag %)) yodaodict)
        phonetic-symbol (-> phonetic-symbol first :content first)
        yodao-web-dict (first (filter #(= :yodao-web-dict (:tag %)) yodaodict))
        web-translations (:content yodao-web-dict)
        web-translations (map #(-> % :content) web-translations)
        web-translations (map (fn [web-trans]
                                (let [web-word (filter #(= :key (:tag %)) web-trans)
                                      web-word (-> web-word first :content first)
                                      transes (filter #(= :trans (:tag %)) web-trans)
                                      transes (map #(-> % :content first :content first) transes)
                                      ]
                                  {:web-word web-word :transes transes}))
                              web-translations)]
    {:custom-translations translations
     :web-translations web-translations
     :phonetic-symbol phonetic-symbol}))

(defn search [word]
  (let [trans-result (fetch word)
        translations (:custom-translations trans-result)
        phonetic-symbol (:phonetic-symbol trans-result)
        web-translations (:web-translations trans-result)]
    (println "----- 发音 -----")
    (println "\t" phonetic-symbol)
    (println "----- 标准解释 -----")
    (doseq [translation translations]
      (println "\t" translation))
    (println "----- 网络解释 -----")
    (doseq [translation web-translations]
      (let [web-word (:web-word translation)
            transes (:transes translation)]
        (print "\t" web-word " : ")
        (doseq [trans transes]
          (print trans ";")))
      (println))))

(defn -main [word]
  (search word))
