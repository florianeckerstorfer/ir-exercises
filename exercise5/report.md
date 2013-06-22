# Information Retrieval, Exercise 5

- Florian Eckerstorfer, [florian@eckerstorfer.co](florian@eckerstorfer.co)
- Haichao Miao, [hmiao87@gmail.com](hmiao87@gmail.com)

## Query Songs

1. Jazz/01 Chasing Pirates.mp3 (by Norah Jones)
2. Jazz/07 Never As Good As The First Time (by Sade)
3. Classic/03 Symphony No.3 In E Flat Major (by Ludwig van Beethoven)
4. Classic/Eine Kleine Nachtmusik (by Wolfgang A. Mozart)
5. Heavy Metal/02 Motor Discipline (by Motorjesus)
6. Heavy Metal/03 Broken, Beat And Scarred (by Metallic)
7. Electro/02 Raw Chicken (by Feed Me)
8. Electro/07 Get Loose (by Benny Benassi)
9. Hip Hop/05 Ms. Jackson (by Outkast)
10. Hip Hop/07 Intergalactic (by Beastie Boys)

## Feature Sets & Distance Metrics

- Rhythm Pattern (RP) with L2 metric
- Rhythm Histogram (RH) with L2 metric
- Rhythm Histogram (RH) with cosine metric

## Analysis

The differences between the RH and the RP are very little. They both retrieve songs from the same genres with a very high similarity, but the RH performed even better when it comes to exclude non-alike songs at the lower ranks. RP seems to low-rank songs that are more similar to the query that songs that are higher ranked. 

The biggest difference is between the cosine and L2 metric. L2 performs much better than cosine ranking. The cosine distance range are very small and the results are very poor. The Electro genre seems to always score very high in many songs, even when it's a completely different genre with very little to no resemblance for the human listener. 

The RH feature set in combination with L2 seems to produce the most useful results. It captures our perception of similarity the best for Classic and Heavy Metal. Those are probably the genres that are the most different from the other genres and therefore they are easier to recognise by the algorithm. Genres like Hip Hop, Electro and Jazz seems to have more resemblance. Songs like "Chasing Pirates" by Norah Jones seems not to match well. The reason therefor is, that the genre of this particular song is not easy to distinguish. It is a fusion of the genres Jazz and Pop with a few Electro elements. The use of synthesiser is very prominent and not representative for songs of this genre. That's why it show resemblance with songs from the genre Hip Hop and even Electro at higher ranks.

The retrieved songs for the Electro genre were probably the best results we retrieved. For both query songs from the genre the algorithms returned a lot of very similar songs. It may be the case that these Electro songs are in fact very similar. Some of the songs could be parts of the same song, we couldn't hear any differences in them.

Both the RH and RP feature sets could not retrieve similar songs for the Hip Hop queries we choose. Both feature sets returned for Intergalactic by Beastie Boys mostly songs by Norah Jones. RP returned not a single Hip Hop song in the top 10 results. This query was also the only query where RH with cosine distance metric performed better than RH and RP with L2 distance metric. The cosine distance metric at least returned some songs from the same artist (Beastie Boys). However, the best ranks in the list were filled with electro songs that didn't sound very similar to Intergalactic for us.

The songs retrieved for the 9th query, Ms. Jackson by Outkast, were a little bit better, but still not what we would have found as similar songs. 

## Conclusion

The adequcy of the metric is very dependend on genre of the query song and the songs in the test corpus. Resemblance between songs for the human is a subjective. The characteristics of a song is undeniably very complex. Different metrics and feature sets might focus on different aspects of these chracteristics. We got very different results from different feature sets, distance metrics and genres. First of all, the cosine distance metric produced very bad results for our test collection. The RH and RP feature sets very similar good or bad, depending on the genre. Both produced quite good results for the Classic, Heave Metal and Electro genre and very bad results for one of the Hip Hop queries. There must be some underlying similarity between the song Intergalactic from the Beastie Boys and Norah Jones that both feature sets thought them as similar. However, it was not possible for us to hear the similarity. All in all, the automatic genre classification works well if the adequate feature set and metric are chosen. 

