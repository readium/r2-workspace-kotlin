[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](/LICENSE)
# r2-workspace-kotlin
A workspace for on-boarding developers

This workspace is one of the ways you can get started with the R2 Kotlin Projects. It is not the only way and you can pick to setup your developement environment in the way that best fits your needs.

This is one of the 4 Github branches in this workspace:

### dev/testapp
You pick this branch if are an developer and a R2 contributor. You can get started quickly on the R2 Testapp and it's modules with this branch. We do appreciate any contribution, no matter how small it is, it makes a difference :) 

1. git clone --recurse-submodules -b dev/testapp https://github.com/readium/r2-workspace-kotlin.git r2-workspace-kotlin
2. ckeckout **dev/testapp** branch **including it's submodules**
3. in Android Studio **Open an existing Android Studio project** and select the **r2-workspace-kotlin** directory
4. Select Build Variant **devTestappDebug** for all Modules (View -> Top Windows -> Build Variants)
5. create a Run configuration (if no configuration exists)
   1. Add new **Android App** and name it
   2. Select Module **r2-testapp** 
6. run confuguration **r2-testapp** 
