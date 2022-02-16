import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    vcsRoot(HttpsGithubComAlexBaturoExampleTeamcityGitRefsHeadsMaster)

    buildType(Test_2)
    buildType(Test)
}

object Test : BuildType({
    name = "Build master and deploy to Nexus"

    vcs {
        root(HttpsGithubComAlexBaturoExampleTeamcityGitRefsHeadsMaster)
    }

    steps {
        maven {
            name = "Build master and deploy to Nexus"

            conditions {
                equals("teamcity.build.branch", "master")
            }
            goals = "clean deploy"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            userSettingsSelection = "settings.xml"
        }
        maven {
            name = "Test another branch"
            executionMode = BuildStep.ExecutionMode.ALWAYS

            conditions {
                doesNotEqual("teamcity.build.branch", "master")
            }
            goals = "clean test"
        }
    }

    triggers {
        vcs {
        }
    }
})

object Test_2 : BuildType({
    name = "Test"

    vcs {
        root(HttpsGithubComAlexBaturoExampleTeamcityGitRefsHeadsMaster)
    }

    triggers {
        vcs {
        }
    }
})

object HttpsGithubComAlexBaturoExampleTeamcityGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/AlexBaturo/example-teamcity.git#refs/heads/master"
    url = "https://github.com/AlexBaturo/example-teamcity.git"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
    authMethod = password {
        userName = "AlexBaturo"
        password = "credentialsJSON:b84d4a33-6fba-48f3-ba04-79372ed9495d"
    }
})
