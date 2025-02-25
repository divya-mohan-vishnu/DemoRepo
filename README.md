Pre-requisites : 
	1. Install Git on Windows
	2. Configure email and username by running below commands in cmd
		git config --global user.email "divya_mohan@yahoo.com"
		git config --global user.name "divya-mohan-vishnu"


GITHUB : simply a website where we can use GIT Tool (www.github.com)
GIT : source control repository/version control repository, where we can use GIT commands push,pull requests
1. Create a new git repository in git
		1.1 click on new repository
		1.2 add repository name and description
 
 
2. Push an existing repository from the command line

		1. I have a maven java project
		2. Initialize Git using git init command
		3. Register existing repository with the gitrepository(DemoRepo) on the github under my account(divya-mohan-vishnu)
		   using the below command
			git remote add origin https://github.com/divya-mohan-vishnu/DemoRepo.git
		4. check what are the files are pending to commit using git status command
		5. Add the comple project directory to git using git add .	
		6. Commit the code using git commit -m "my first git commit"	(now the code is committed , but not pushed to repository)
		7. Push the code using command git push origin master  (master is the default branch name)
		
		Git Commands
		1. git init
		2. git remote add origin https://github.com/divya-mohan-vishnu/DemoRepo.git
		3. git status
		4. git add .
		5. git status
		6. git commit -m "test"
		7. git push master origin
