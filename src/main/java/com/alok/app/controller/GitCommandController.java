package com.alok.app.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alok.parser.JavaClassPaser;

@RestController
public class GitCommandController {

	// @Value("${git.complete.path}")
	private String gitRepoPath = "C:\\Users\\i529560\\eclipse-workspace1\\SpringBootWithGradle";

	// @Value("${git.diff.only.java.files}")
	private boolean includeOnlyJavaFilesDiff;

	private Git git;

	private Repository repo;

	@GetMapping("/runReleventTests")
	ResponseEntity<String> generateTests(@RequestParam(name = "commitId", required = false) String commitId) {
		String response = "STARTED.";
		try {
			repo = getRepository();
			System.out.println("Git Branch : " + repo.getBranch());

			git = new Git(repo);
			List<String> commitsUnderTest = new LinkedList<>();
			for (RevCommit commit : git.log().add(repo.resolve(repo.getBranch())).setMaxCount(2).call()) {
				System.out.println("Commit : " + commit.getName());
				commitsUnderTest.add(commit.getName());
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
			System.out.println("command ::" + diffCommand.toString());

			if (includeOnlyJavaFilesDiff)
				diffCommand.setPathFilter(PathSuffixFilter.create(".java"));

			List<DiffEntry> diff = diffCommand.call();
			List<String> classFileList = new ArrayList<>();
			for (DiffEntry entry : diff) {

				System.out.println("-------------------------------------------");
				System.out.println("Attribute::"+entry.getNewPath());
				JavaClassPaser.parseJavaFile(entry.getNewPath());
				classFileList.add(entry.getNewPath());
				//System.out.println("Attribute::"+entry.());
				System.out.println("-------------------------------------------");
				stringBuilder.append("Change Type: " + entry.getChangeType()
						+ (!(entry.getChangeType().equals(DiffEntry.ChangeType.ADD))
								? ", existing " + entry.getOldPath()
								: "")
						+ ((!entry.getOldPath().equals(entry.getNewPath())) ? ", to: " + entry.getNewPath() : ""));
				stringBuilder.append("\r\n<br>");

			}
		}
		System.out.println(stringBuilder.toString());
		printDiff1(repository, commits.get(0));
		return printDiff(repository, commits.get(0));
		//return stringBuilder.toString();
	}

	public String printDiff(Repository repository, String commitId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>\n");
		stringBuilder.append("<body>\n");
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("git", "diff", commitId + "~", commitId);
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("<br>\n");
			}
			if(process.waitFor()!=0) {
				throw new RuntimeException("failure of command execution");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stringBuilder.append("</html>\n");
		stringBuilder.append("</body>\n");
		return stringBuilder.toString();
	}

	private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
		// from the commit we can build the tree which allows us to construct the
		// TreeParser
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
	
	
	public String printDiff1(Repository repository, String commitId) {
		StringBuilder stringBuilder = new StringBuilder();
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("git", "diff", commitId + "~", commitId);
		List<Integer> list = new ArrayList<>();
		try {
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			int count =0;
			while ((line = reader.readLine()) != null) {
				int startingPoint =0;
				int endPoint = 0;
				count = count+1;
				if(line.startsWith("@@")) {
					count=0;
					System.out.println(line);
					String[] str = line.split(" ");
					System.out.println(Arrays.toString(str));
					System.out.println(str[2]);
					String[] str1 = str[2].split(",");
					System.out.println(Arrays.toString(str1));
					startingPoint = Integer.valueOf(str1[0]); 
					endPoint = Integer.valueOf(str1[1]); 
				}
				if(line.startsWith("+")||line.startsWith("-")) {
					System.out.println("line::"+line);
					list.add(count+startingPoint);
				}
				
			}
			if(process.waitFor()!=0) {
				throw new RuntimeException("failure of command execution");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("List::"+list);
		return stringBuilder.toString();
	}

}
