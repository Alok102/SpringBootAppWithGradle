package com.alok.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitDiffCommand {

	@Value("${git.complete.path}")
	private String gitRepoPath;

	@Value("${git.diff.only.java.files}")
	private boolean includeOnlyJavaFilesDiff;

	@GetMapping("/runReleventTests")
	ResponseEntity<String> generateTests(@RequestParam(name = "commitId", required = false) String commitId) {
		String response = "STARTED.";
		try {
			Repository repo = getRepository();
			System.out.println("Git Branch : " + repo.getBranch());

			Git git = new Git(repo);
			int count = 0;
			List<String> commitsUnderTest = new LinkedList<>();
			for (RevCommit commit : git.log().add(repo.resolve(repo.getBranch())).setMaxCount(2).call()) {
				System.out.println("Commit : " + commit.getName());
				commitsUnderTest.add(commit.getName());
				count++;
			}

			response = printDiff(repo, commitsUnderTest);
		} catch (IOException ioexception) {
			response = "generateTests exp :" + ioexception;
			System.out.println(response);

		} catch (NoHeadException e) {
			response = "generateTests exp :" + e;
			System.out.println(response);
		} catch (GitAPIException e) {
			response = "generateTests exp :" + e;
			System.out.println(response);
		} finally {
			return new ResponseEntity<String>(response, HttpStatus.OK);

		}
	}

	public String printDiff(Repository repository, List<String> commits) throws IOException, GitAPIException {
		if (commits.size() < 2) {
			System.out.println("invalid");
			return "Did Not find Two commits";
		}
		StringBuilder stringBuilder = new StringBuilder();

		AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, commits.get(1));
		AbstractTreeIterator newTreeParser = prepareTreeParser(repository, commits.get(0));

		// then the porcelain diff-command returns a list of diff entries
		try (Git git = new Git(repository)) {

			DiffCommand diffCommand = git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser)
					.setShowNameAndStatusOnly(true);

			if (includeOnlyJavaFilesDiff)
				diffCommand.setPathFilter(PathSuffixFilter.create(".java"));

			List<DiffEntry> diff = diffCommand.call();
			for (DiffEntry entry : diff) {

				stringBuilder.append("Change Type: " + entry.getChangeType()
						+ (!(entry.getChangeType().equals(DiffEntry.ChangeType.ADD))
								? ", existing " + entry.getOldPath()
								: "")
						+ ((!entry.getOldPath().equals(entry.getNewPath())) ? ", to: " + entry.getNewPath() : ""));
				stringBuilder.append("\r\n<br>");

			}
		}
		System.out.println(stringBuilder.toString());
		return stringBuilder.toString();
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser
		// noinspection Duplicates
		try (RevWalk walk = new RevWalk(repository)) {
			RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
			RevTree tree = walk.parseTree(commit.getTree().getId());

			CanonicalTreeParser treeParser = new CanonicalTreeParser();
			try (ObjectReader reader = repository.newObjectReader()) {
				treeParser.reset(reader, tree.getId());
			}

			walk.dispose();

			return treeParser;
		}
	}

	private Repository getRepository() throws IOException {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
		repositoryBuilder.setMustExist(true);
		repositoryBuilder.setGitDir(new File(gitRepoPath + "/.git"));
		Repository repository = repositoryBuilder.build();
		return repository;
	}

}
