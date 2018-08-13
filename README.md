[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](/LICENSE)
# r2-workspace-kotlin
A workspace for on-boarding developers

This workspace is one of the ways you can get started with the R2 Kotlin Projects. It is not the only way and you can pick to setup your developement environment in the way that best fits your needs.

In this workspace you will find 4 Github branches:

### int/testapp
You pick this branch if are an integrator and all you need is to quickly get started with the R2 Testapp. 

1. git clone --recurse-submodules -b int/testapp https://github.com/readium/r2-workspace-kotlin.git r2-workspace-kotlin
2. ckeckout **int/testapp** branch **including it's submodules**
3. in Android Studio **Open an existing Android Studio project** and select the **r2-workspace-kotlin** directory
4. Select Build Variant **intTestappDebug** for all Modules
5. create a Run configuration (if no configuration exists)
   1. Add new **Android App** and name it
   2. Select Module **r2-testapp** 
6. run confuguration **r2-testapp** 
  


### dev/testapp
You pick this branch if are an developer and a R2 contributor. You can get started quickly on the R2 Testapp and it's modules with this branch. We do appreciate any contribution, no matter how small it is, it makes a difference :) 

1. git clone --recurse-submodules -b dev/testapp https://github.com/readium/r2-workspace-kotlin.git r2-workspace-kotlin
2. ckeckout **dev/testapp** branch **including it's submodules**
3. in Android Studio **Open an existing Android Studio project** and select the **r2-workspace-kotlin** directory
4. Select Build Variant **devTestappDebug** for all Modules
5. create a Run configuration (if no configuration exists)
   1. Add new **Android App** and name it
   2. Select Module **r2-testapp** 
6. run confuguration **r2-testapp** 



### int/testapp-with-lcp
You pick this branch if are an integrator and you would like to get started with the R2 Testapp supporting LCP. 

1. git clone --recurse-submodules -b int/testapp-with-lcp https://github.com/readium/r2-workspace-kotlin.git r2-workspace-kotlin
2. ckeckout **int/testapp-with-lcp** branch **including it's submodules**
3. in Android Studio **Open an existing Android Studio project** and select the **r2-workspace-kotlin** directory
4. Select Build Variant **intTestappWithLcpDebug** for all Modules
5. create a Run configuration (if no configuration exists)
   1. Add new **Android App** and name it
   2. Select Module **r2-testapp** 
6. copy **liblcp.aar** into the **liblcp** directory
7. run confuguration **r2-testapp** 


### dev/testapp-with-lcp
You pick this branch if are an developer and a R2 contributor. You can get started quickly on the R2 Testapp and it's modules including LCP with this branch. We do appreciate any contribution, no matter how small it is, it makes a difference :) 

1. git clone --recurse-submodules -b dev/testapp-with-lcp https://github.com/readium/r2-workspace-kotlin.git r2-workspace-kotlin
2. ckeckout **dev/testapp-with-lcp** branch **including it's submodules**
3. in Android Studio **Open an existing Android Studio project** and select the **r2-workspace-kotlin** directory
4. Select Build Variant **devTestappWithLcpDebug** for all Modules
5. create a Run configuration (if no configuration exists)
   1. Add new **Android App** and name it
   2. Select Module **r2-testapp** 
6. copy **liblcp.aar** into the **liblcp** directory
7. run confuguration **r2-testapp** 



## Getting the pre-compiled Readium LCP module.

Readium LCP is a DRM. As any protection technology, care must be taken to avoid disclosing too much technical information to the open world without good reasons. This is why EDRLab has chosen to provide a small pre-compiled Readium LCP module to R2 implementers, in two flavors:

1. a "test" grade LCP library, provided after a direct contact between the developer and EDRLab. To get this library, a developer must only provide to EDRLab its company name, web site and basic information about the context of the project involving Readium LCP.
2.  a "production" grade LCP library, which simply replaces the "test" library in the project after the development has been fully tested. Go get access to this production module, the client company must be trusted and the Readium LCP Terms of Used must have been signed.

To contact EDRLab, please send an email to contact(at)edrlab.org or use the Readium Slack on the "lcp" channel.

