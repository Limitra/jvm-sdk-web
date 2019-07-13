package com.limitra.sdk.web.task

import play.api.inject.{SimpleModule, _}

class Module extends SimpleModule(bind[FileStorageTask].toSelf.eagerly())
