<div>
  Current Revision Extractor field : <input class="input-box" type="text" name="buildRevision" style="width:300px;"
                                             value="${bamboo.planRepository.revision}" placeholder="bamboo.planRepository.revision"/>
  <br><br>
  Previous Revision Extractor field : <input type="text" name="buildPreviousRevision" style="width:300px;"
                                             value="${bamboo.planRepository.previousRevision}" placeholder="bamboo.planRepository.previousRevision"/>
  <br><br>
  Release Label Extractor field : <input type="text" name="releaseLabel" style="width:400px;"
                                         value="${bamboo.inject.rel_ver}" placeholder="bamboo.inject.rel_ver"/>
  <br><br>
  Release note Source path : <input type="text" name="sourceFileLocation" style="width:500px;" value=""/>
  <br><br>
  Release note Destination path : <input type="text" name="destinationFileLocation" style="width:500px;" value=""/>
</div>