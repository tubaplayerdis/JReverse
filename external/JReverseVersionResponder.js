function doGet() {
  const pubFolder = DriveApp.getFolderById("17zo2Ph-l5lG9CZxYdyEnQXayaZ_Bs23J")

  var pubfileDataArray = []

  var pubFolders = pubFolder.getFolders()

  while(pubFolders.hasNext()) {
    var folder = pubFolders.next();
    var files = folder.getFiles();
    while(files.hasNext()) {
      var file = files.next()
      const filedata = getFileData(file.getName(), file.getDateCreated(), folder.getName(), file.getSize(), file.getDownloadUrl())
      pubfileDataArray.push(filedata)
    }
  }

  //Dev

  const devFolder = DriveApp.getFolderById("1SViQOXKMAk_qpoEY_5DnekX5rDE98h7k")
  var devfileDataArray = []
  var devFolders = devFolder.getFolders()

  while (devFolders.hasNext()) {
    var folder = devFolders.next();
    var files = folder.getFiles();
    while(files.hasNext()) {
      var file = files.next()
      const filedata = getFileData(file.getName(), file.getDateCreated(), folder.getName(), file.getSize(), file.getDownloadUrl())
      devfileDataArray.push(filedata)
    }
  }

  const fullDataJson = {
    dev: [],
    pub: []
  }
  fullDataJson.dev = devfileDataArray
  fullDataJson.pub = pubfileDataArray

  Logger.log(JSON.stringify(fullDataJson))
  const returnable = ContentService.createTextOutput(JSON.stringify(fullDataJson)).setMimeType(ContentService.MimeType.JSON)
  Logger.log(returnable)
  return returnable
}

function getFileData(Name, Date, Version, Size, Downloadlink) {
    return {
        name: Name,
        date: Date,
        version: Version,
        size: Size,
        downloadlink: Downloadlink
    };
}
