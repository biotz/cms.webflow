# magnet/cms.webflow
[![Build Status](https://api.travis-ci.com/magnetcoop/cms.webflow.svg?branch=master)](https://travis-ci.com/magnetcoop/cms.webflow)
[![Clojars Project](https://img.shields.io/clojars/v/magnet/cms.webflow.svg)](https://clojars.org/magnet/cms.webflow)

A [Duct](https://github.com/duct-framework/duct) library that provides an [Integrant](https://github.com/weavejester/integrant) key for interacting with the Webflow CMS API.

## Table of contents
* [Installation](#installation)
* [Usage](#usage)
  * [Configuration](#configuration)
  * [Obtaining a Webflow record](#obtaining-a-webflow-record)
  * [Available methods](#available-methods)
    
## Installation

[![Clojars Project](https://clojars.org/magnet/cms.webflow/latest-version.svg)](https://clojars.org/magnet/cms.webflow)

## Usage

### Configuration
To use this library add the following key to your configuration:

`:magnet.cms/webflow`

This key expects a configuration map with two mandatory keys, plus another three optional ones.
These are the mandatory keys:

* `:site-id`: the id of the site to be managed
* `:api-token` : the [token](https://developers.webflow.com/oauth#api-keys) needed to authenticate against Webflow CMS API

These are the optional keys:
* `:timeout`: Timeout value (in milli-seconds) for an connection attempt with Webflow.
* `:max-retries`: If the connection attempt fails, how many retries we want to attempt before giving up.
* `:backoff-ms`: This is a vector in the form [initial-delay-ms max-delay-ms multiplier] to control the delay between each retry. The delay for nth retry will be (max (* initial-delay-ms n multiplier) max-delay-ms). If multiplier is not specified (or if it is nil), a multiplier of 2 is used. All times are in milli-seconds.

Key initialization returns a `Webflow` record that can be used to perform the Webflow CMS operations described below.

#### Configuration example
Basic configuration:
```edn
  :magnet.cms/webflow
   {:site-id  #duct/env ["WEBFLOW_SITE_ID" Str :or "5f7a9571fedvb6Aabb00c55f"]
    :api-token #duct/env ["WEBFLOW_API_TOKEN" Str :or "4f17e5522fa44e82b102644203009193209ac5eb3d577774d63ab29e093a9496"]}
```

Configuration with custom request retry policy:
```edn
  :magnet.cms/webflow
   {:site-id  #duct/env ["WEBFLOW_SITE_ID" Str :or "5f7a9571fedvb6Aabb00c55f"]
    :api-token #duct/env ["WEBFLOW_API_TOKEN" Str :or "4f17e5522fa44e82b102644203009193209ac5eb3d577774d63ab29e093a9496"]
    :timeout 300
    :max-retries 5
    :backoff-ms [10 500]}
```

### Obtaining a `Webflow` record

If you are using the library as part of a [Duct](https://github.com/duct-framework/duct)-based project, adding any of the previous configurations to your `config.edn` file will perform all the steps necessary to initialize the key and return a `Webflow` record for the associated configuration. In order to show a few interactive usages of the library, we will do all the steps manually in the REPL.

First we require the relevant namespaces:

```clj
user> (require '[integrant.core :as ig]
               '[magnet.cms.core :as core])
nil
user>
```

Next we create the configuration var holding the Webflow integration configuration details:

```clj
user> (def config {:site-id  #duct/env ["WEBFLOW_SITE_ID" Str :or "5f7a9571fedvb6Aabb00c55f"]
                   :api-token #duct/env ["WEBFLOW_API_TOKEN" Str :or "4f17e5522fa44e82b102644203009193209ac5eb3d577774d63ab29e093a9496"]})
#'user/config
user>
```

Now that we have all pieces in place, we can initialize the `:magnet.cms/webflow` Integrant key to get a `Webflow` record. As we are doing all this from the REPL, we have to manually require `magnet.cms.webflow` namespace, where the `init-key` multimethod for that key is defined (this is not needed when Duct takes care of initializing the key as part of the application start up):

``` clj
user> (require '[magnet.cms.webflow :as webflow])
nil
user>
```

And we finally initialize the key with the configuration defined above, to get our `Webflow` record:

``` clj
user> (def wf-record (->
                       config
                       (->> (ig/init-key :magnet.cms/webflow))))
#'user/wf-record
user> wf-record
#magnet.cms.webflow.Webflow{:api-token "4f17e5522fa44e82b102644203009193209ac5eb3d577774d63ab29e093a9496",
                            :site-id "5f7a9571fedvb6Aabb00c55f",
                            :timeout 2000,
                            :max-retries 10,
                            :backoff-ms [500 1000 2.0]}
user>
```
Now that we have our `Webflow` record, we are ready to use the methods defined by the protocols defined in `magnet.cms.core` namespace.

### Available methods

This are the methods available to interact with the Webflow CMS API. The mapping for the methods is one to one, so refer to the Webflow official documentation for details.

  * [Collections](https://developers.webflow.com/?shell#collections)
    * [(get-all-collections wf-record)](https://developers.webflow.com/?shell#list-collections)
    * [(get-collection wf-record collection-id)](https://developers.webflow.com/?shell#get-collection-with-full-schema)
  * [Items](https://developers.webflow.com/?shell#items)
    * [(get-items wf-record collection-id)](https://developers.webflow.com/?shell#get-all-items-for-a-collection)
    * [(get-item wf-record collection-id item-id)](https://developers.webflow.com/?shell#get-single-item)  
  * [E-commerce](https://developers.webflow.com/?shell#ecommerce)
    * [(get-all-orders wf-record)](https://developers.webflow.com/?shell#get-all-orders)
    * [(get-order wf-record order-id)](https://developers.webflow.com/?shell#get-order)  
    * [(update-order wf-record order-id fields)](https://developers.webflow.com/?shell#update-order)
    * [(fulfill-order wf-record order-id)](https://developers.webflow.com/?shell#fulfill-order)  
    * [(unfulfill-order wf-record order-id)](https://developers.webflow.com/?shell#unfulfill-order)
    * [(refund-order wf-record order-id)](https://developers.webflow.com/?shell#refund-order)
    * [(get-item-inventory wf-record collection-id item-id)](https://developers.webflow.com/?shell#item-inventory)
    * [(update-item-inventory wf-record collection-id item-id)](https://developers.webflow.com/?shell#update-item-inventory)
    
All the responses will include a `:success?` key. When `:success?` is `false` `:reason` and `error-details` keys will be included. The possible reasons can be: `:bad-request`, `not-found`, `access-denied` and `error`. The `error-details` will include a map with the error information provided by the Webflow API.

## License

Copyright (c) 2019 Magnet S Coop.

This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/
