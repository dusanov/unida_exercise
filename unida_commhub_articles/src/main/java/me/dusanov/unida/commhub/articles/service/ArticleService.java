package me.dusanov.unida.commhub.articles.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.dusanov.unida.commhub.articles.model.Article;
import me.dusanov.unida.commhub.articles.repo.ArticleRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Transactional
//@Slf4j
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepo articleRepo;
	
	public Mono<Article> createArticle(Article article){
		return articleRepo.save(article);
	}
	
	public Flux<Article> getAllArticles(){
		return articleRepo.findAll();
	}
	
	public Mono<Article> findById(Integer articleId){
		return articleRepo.findById(articleId);
	}
	
	public Mono<Article> updateArticle(Integer articleId, Article article){
		return articleRepo.findById(articleId)
				.flatMap(dbArticle -> {
					dbArticle.setArticleTitle(article.getArticleTitle());
					dbArticle.setArticleDesc(article.getArticleDesc());
					dbArticle.setArticleText(article.getArticleText());
					dbArticle.setArticleImage(article.getArticleImage());
					return articleRepo.save(dbArticle);
				});
	}
	
	public Mono<Article> deleteArticle(Integer articleId){
		return articleRepo.findById(articleId)
				.flatMap(dbArticle -> articleRepo.delete(dbArticle)
				.then(Mono.just(dbArticle)));
	}
	
	public Flux<Article> fetchArticles(List<Integer> articleIds){
		return Flux.fromIterable(articleIds)
				.parallel()
				.runOn(Schedulers.elastic())
				.flatMap(articleId -> articleRepo.findById(articleId))
				.ordered((article1, article2) -> article2.getId() - article1.getId());
	}
}
